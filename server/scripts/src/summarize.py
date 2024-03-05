import sys
import os
from typing import Union, List
from urllib.parse import urlparse
import cmdline as cl

from langchain_community.document_loaders import YoutubeLoader
from langchain_community.document_loaders import UnstructuredPowerPointLoader
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_community.chat_models import ChatOpenAI
from langchain.chains import LLMChain
from dotenv import find_dotenv, load_dotenv
from PyPDF2 import PdfReader, PdfWriter
from langchain.prompts.chat import (
    ChatPromptTemplate,
    SystemMessagePromptTemplate,
    HumanMessagePromptTemplate,
)
import textwrap

embeddings = None

def create_embeddings(options):
    """
    Create OpenAI embeddings.

    Parameters:
    - `options`: User-defined options.
    """

    global embeddings

    if 'env' in options:
        load_dotenv(options['env'])
    else:
        load_dotenv(find_dotenv())
    embeddings = OpenAIEmbeddings()

def create_db_from_youtube_video_url(video_url: str) -> tuple[FAISS, List]:
    """
    Create an FAISS index and list of documents from a YouTube video URL.

    Parameters:
    - `video_url`: YouTube video URL.

    Returns:
    A tuple containing the FAISS index and a list of documents.
    """
    print("Create db from youtube video url")

    loader = YoutubeLoader.from_youtube_url(video_url)
    transcript = loader.load()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(transcript)

    db = FAISS.from_documents(docs, embeddings)
    if db is None:
        raise Exception("Failed to create FAISS index")

    return db, docs


def extract_youtube_link_from_file(file_path: str) -> str:
    """
    Extract a YouTube video URL from a file.

    Parameters:
    - `file_path`: Path to the file containing the YouTube video URL.

    Returns:
    The extracted YouTube video URL.
    """
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            content = file.read().strip()
            return content
    except Exception as e:
        print(f"Error reading file: {e}")
        sys.exit(1)


def is_youtube_link(input_str: str) -> bool:
    """
    Check if the input string is a valid YouTube video URL.

    Parameters:
    - `input_str`: Input string to check.

    Returns:
    True if the input string is a valid YouTube video URL, False otherwise.
    """
    parsed_url = urlparse(input_str)
    return (
            parsed_url.netloc == "www.youtube.com"
            and "/watch" in parsed_url.path
            and "v=" in parsed_url.query
    )


def create_db_from_powerpoint_file(pptx_file: str) -> tuple[FAISS, List]:
    """
    Create an FAISS index and list of documents from a PowerPoint file.

    Parameters:
    - `pptx_file`: Path to the PowerPoint file.

    Returns:
    A tuple containing the FAISS index and a list of documents.
    """
    global embeddings

    print("Create db from powerpoint file")

    loader = UnstructuredPowerPointLoader(pptx_file)
    data = loader.load()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(data)

    db = FAISS.from_documents(docs, embeddings)
    if db is None:
        raise Exception("Failed to create FAISS index")

    return db, docs


def create_db_from_pdf(pdf_file: str) -> tuple[FAISS, List]:
    """
    Create an FAISS index and list of documents from a PDF file.

    Parameters:
    - `pdf_file`: Path to the PDF file.

    Returns:
    A tuple containing the FAISS index and a list of documents.
    """
    global embeddings

    print("Create db from pdf")

    loader = PyPDFLoader(pdf_file)
    pages = loader.load_and_split()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(pages)

    db = FAISS.from_documents(docs, embeddings)
    if db is None:
        raise Exception("Failed to create FAISS index")

    return db, docs


def get_response_from_query(db: FAISS, query: str, k: int = 4) -> tuple[str, List]:
    """
    Get a response and list of documents from a query using an FAISS index.

    Parameters:
    - `db`: FAISS index.
    - `query`: Query string.
    - `k`: Number of documents to retrieve.

    Returns:
    A tuple containing the response and a list of documents.
    """

    print("Similarity search...")
    docs = db.similarity_search(query, k=k)
    docs_page_content = " ".join([d.page_content for d in docs])

    print("Initializing chat model...")
    chat = ChatOpenAI(model_name="gpt-4", temperature=0.2)

    # Template to use for the system message prompt
    template = """
        You are a helpful assistant that can answer questions about YouTube videos, PDFs, and PowerPoint files 
        based on: {docs}

        Only use the factual information from the transcript to answer the question.

        

        """

    system_message_prompt = SystemMessagePromptTemplate.from_template(template)

    # Human question prompt
    human_template = "Answer the following question: {question}"
    human_message_prompt = HumanMessagePromptTemplate.from_template(human_template)

    chat_prompt = ChatPromptTemplate.from_messages(
        [system_message_prompt, human_message_prompt]
    )

    chain = LLMChain(llm=chat, prompt=chat_prompt)

    print("Running chat model...")
    response = chain.run(question=query, docs=docs_page_content)
    return response, docs

def merge_pdfs(input_pdfs: List[str], output_pdf: str):
    """
    Merge multiple PDFs into a single PDF.

    Parameters:
    - `input_pdfs`: List of paths to input PDFs.
    - `output_pdf`: Path to the merged output PDF.
    """
    print("Detected multiple PDFs")
    pdf_writer = PdfWriter()

    for pdf_path in input_pdfs:
        with open(pdf_path, 'rb') as pdf_file:
            pdf_reader = PdfReader(pdf_file)
            for page_num in range(len(pdf_reader.pages)):
                pdf_writer.add_page(pdf_reader.pages[page_num])

    with open(output_pdf, 'wb') as output_file:
        pdf_writer.write(output_file)


def extract_text_from_pdf(pdf_path):
    with open(pdf_path, 'rb') as pdf_file:
        pdf_reader = PdfReader(pdf_file)
        num_pages = len(pdf_reader.pages)

        for page_num in range(num_pages):
            page = pdf_reader.pages[page_num]
            text = page.extract_text()
            print(f'Page {page_num + 1}:\n{text}\n{"-" * 50}\n')

def process_path(inputs: list[str], output: str, options: dict[str, cl.SwitchValue]) -> None:
    """
    Process a path based on user-defined options.

    Parameters:
    - `inputs`: List of input paths.
    - `output`: Output path.
    """

    multiple_pdfs = False

    # Merge PDFs if multiple input PDFs are provided
    if len(inputs) > 1:
        merged_pdf_path = "../test/combined.pdf"
        merge_pdfs(inputs, merged_pdf_path)
        inputs = [merged_pdf_path]
        multiple_pdfs = True

    """ DEBUGGING """
    # extract_text_from_pdf(merged_pdf_path)

    path = inputs[0]  # for now, only one input

    _, ext = os.path.splitext(path)

    if ext == ".pptx":
        create_embeddings(options)
        db, _ = create_db_from_powerpoint_file(path)
    elif ext == ".pdf":
        create_embeddings(options)
        db, _ = create_db_from_pdf(path)
    elif is_youtube_link(path):
        create_embeddings(options)
        db, _ = create_db_from_youtube_video_url(path)
    else:
        raise Exception("Summarizer requires a YouTube video URL, PDF, or PowerPoint file.")

    # Linear interpolation for verbose switch
    verbosity_min = 300
    verbosity_max = 1000
    verbosity = verbosity_min + (options["verbose"] * (verbosity_max - verbosity_min))
    query = f"Take notes on this video/PDF/PowerPoint in this format. Limit the output to {int(verbosity)} words. Make sure that you properly newline throughout the page: """

    if multiple_pdfs:
        query += "This PDF contains information from multiple PDFs. Make sure to understand information about all of them and to generate notes based on the content of all the PDFs"

    query += """# [Title]"""

    if not options["no_general_overview"]:
        query += """
        ## General Overview
        [Provide a brief summary or introduction of the topic.]
        """
    if not options["no_key_concepts"]:
        query += """
        ## Key Concepts

        - **Concept 1:**
            - [Brief description or explanation of the first key concept.]
        - **Concept 2:**
            - [Brief description or explanation of the second key concept.]
        - **Concept 3:**
            - [Brief description or explanation of the third key concept.]
        """
    if not options["no_section_by_section"]:
        query += """
        ## Section by Section Breakdown

        ### 1. Section One Title

        - [Detailed content or information related to the first section.]

        ### 2. Section Two Title

        - [Detailed content or information related to the second section.]

        ### 3. Section Three Title

        - [Detailed content or information related to the third section.]

        ### n. Section n Title

        - [Detailed content or information related to the nth section]
        """
    if not options["no_additional_information"]:
        query += """
            ## Additional Information

            - [Include any additional points, tips, or related information.]
        """
    if not options["no_helpful_vocabulary"]:
        query += """
            ## Helpful Vocabulary

            - **Term 1:**
                - [Definition or explanation of the first term.]
            - **Term 2:**
                - [Definition or explanation of the second term.]
            - **Term 3:**
                - [Definition or explanation of the third term.]
            - **Term n:**
                - [Definition or explanation of the nth term.]
        """
    if not options["no_explain_to_5th_grader"]:
        query += """
            ## Explain it to a 5th grader:

            [Provide an explanation about this topic suitable for a 5th grader]
        """
    if not options["no_conclusion"]:
        query += """
            ## Conclusion

            [Summarize the key takeaways or concluding remarks.]
        """
    # print(f"Query {query}")

    response, _ = get_response_from_query(db, query)

    print("Writing response to file...")
    with open(output, "w", encoding="utf-8") as file:
        file.write(response)
    print(f"Cleaned response has been saved to: {output}")

def usage():
    print("Usage: python summarize.py [options...] <output> [inputs...]")
    print("Options:")
    print("  --verbose <float>            Set verbosity level (default: 1.0)")
    print("  --no_general_overview        Do not include general overview")
    print("  --no_key_concepts            Do not include key concepts")
    print("  --no_section_by_section      Do not include section by section breakdown")
    print("  --no_additional_information  Do not include additional information")
    print("  --no_helpful_vocabulary      Do not include helpful vocabulary")
    print("  --no_explain_to_5th_grader   Do not include explain to 5th grader")
    print("  --no_conclusion              Do not include conclusion")
    print("  --env <path>                 Path to .env file")
    print("  --out <path>                 Output file")
    print("  --help                       Show this help message and exit")
    return 0

def usage():
    print("Usage: python summarize.py [options...] <output> [inputs...]")
    print("Options:")
    print("  --verbose <float>            Set verbosity level (default: 1.0)")
    print("  --no_general_overview        Do not include general overview")
    print("  --no_key_concepts            Do not include key concepts")
    print("  --no_section_by_section      Do not include section by section breakdown")
    print("  --no_additional_information  Do not include additional information")
    print("  --no_helpful_vocabulary      Do not include helpful vocabulary")
    print("  --no_explain_to_5th_grader   Do not include explain to 5th grader")
    print("  --no_conclusion              Do not include conclusion")
    print("  --env <path>                 Path to .env file")
    print("  --out <path>                 Output file")
    print("  --help                       Show this help message and exit")
    return 0


if __name__ == "__main__":
    switches = [
        cl.Switch("verbose", type=float, value=1.0),
        cl.Switch("no_general_overview", type=bool, value=False),
        cl.Switch("no_key_concepts", type=bool, value=False),
        cl.Switch("no_section_by_section", type=bool, value=False),
        cl.Switch("no_additional_information", type=bool, value=False),
        cl.Switch("no_helpful_vocabulary", type=bool, value=False),
        cl.Switch("no_explain_to_5th_grader", type=bool, value=False),
        cl.Switch("no_conclusion", type=bool, value=False),
        cl.Switch("env", type=str),
        cl.Switch("out", type=str),
        cl.Switch("help", value=usage),
    ]

    r = cl.parse(sys.argv, switches=switches)
    if isinstance(r, int):
        sys.exit(r)

    args, options = r
    if len(args) == 0:
        raise Exception("No input files")

    if options["out"] is None:
        raise Exception("No output file")

    output = options["out"]
    inputs = args

    process_path(inputs, output, options)
    sys.exit(0)
