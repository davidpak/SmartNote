import sys
import os
from typing import Union, List
from urllib.parse import urlparse
from cmdline import Switch, parse_summarize

from langchain_community.document_loaders import YoutubeLoader
from langchain_community.document_loaders import UnstructuredPowerPointLoader
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_community.chat_models import ChatOpenAI
from langchain.chains import LLMChain
from dotenv import find_dotenv, load_dotenv
from langchain.prompts.chat import (
    ChatPromptTemplate,
    SystemMessagePromptTemplate,
    HumanMessagePromptTemplate,
)
import textwrap

embeddings = OpenAIEmbeddings()


def create_db_from_youtube_video_url(video_url: str) -> tuple[FAISS, List]:
    """
    Create an FAISS index and list of documents from a YouTube video URL.

    Parameters:
    - `video_url`: YouTube video URL.

    Returns:
    A tuple containing the FAISS index and a list of documents.
    """
    loader = YoutubeLoader.from_youtube_url(video_url)
    transcript = loader.load()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(transcript)

    db = FAISS.from_documents(docs, embeddings)
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
    loader = UnstructuredPowerPointLoader(pptx_file)
    data = loader.load()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(data)

    db = FAISS.from_documents(docs, embeddings)
    return db, docs


def create_db_from_pdf(pdf_file: str) -> tuple[FAISS, List]:
    """
    Create an FAISS index and list of documents from a PDF file.

    Parameters:
    - `pdf_file`: Path to the PDF file.

    Returns:
    A tuple containing the FAISS index and a list of documents.
    """
    loader = PyPDFLoader(pdf_file)
    pages = loader.load_and_split()

    text_splitter = RecursiveCharacterTextSplitter(chunk_size=2000, chunk_overlap=100)
    docs = text_splitter.split_documents(pages)

    db = FAISS.from_documents(docs, embeddings)
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
    docs = db.similarity_search(query, k=k)
    docs_page_content = " ".join([d.page_content for d in docs])

    chat = ChatOpenAI(model_name="gpt-4", temperature=0.2)

    # Template to use for the system message prompt
    template = """
        You are a helpful assistant that can answer questions about YouTube videos, PDFs, and PowerPoint files 
        based on: {docs}

        Only use the factual information from the transcript to answer the question.

        If you feel like you don't have enough information to answer the question, say "I don't know".

        """

    system_message_prompt = SystemMessagePromptTemplate.from_template(template)

    # Human question prompt
    human_template = "Answer the following question: {question}"
    human_message_prompt = HumanMessagePromptTemplate.from_template(human_template)

    chat_prompt = ChatPromptTemplate.from_messages(
        [system_message_prompt, human_message_prompt]
    )

    chain = LLMChain(llm=chat, prompt=chat_prompt)

    response = chain.run(question=query, docs=docs_page_content)
    return response, docs


def process_path(path: str, output: str, switches: dict) -> None:
    """
    Process a path based on user-defined options.

    Parameters:
    - `path`: Path to the input.
    - `switches`: User-defined options.
    """
    _, ext = os.path.splitext(path)

    if ext == ".pptx":
        db, docs = create_db_from_powerpoint_file(path)
    elif ext == ".pdf":
        db, docs = create_db_from_pdf(path)
    elif is_youtube_link(path):
        try:
            db, docs = create_db_from_youtube_video_url(path)
        except Exception as e:
            print(f"Error processing YouTube link: {e}")
            sys.exit(1)
    else:
        print("Summarizer requires a YouTube video URL, PDF, or PowerPoint file.")
        sys.exit(1)

    args, options = parse_summarize(sys.argv[1:], switches)

    if 'env' in args:
        load_dotenv(args['env'])
    else:
        load_dotenv(find_dotenv())

    # Linear interpolation for verbose switch
    verbosity_min = 300
    verbosity_max = 700
    verbosity = verbosity_min + (options["verbose"] * (verbosity_max - verbosity_min))
    query = f"Take notes on this video/PDF/PowerPoint in this format. Limit the output to {int(verbosity)} words. Make sure that you properly newline throughout the page: """

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
    print(f"Query {query}")

    response, docs = get_response_from_query(db, query)

    with open(output, "w", encoding="utf-8") as file:
        file.write(response)
    print(f"Cleaned response has been saved to: {output}")


if __name__ == "__main__":
    my_switches = [
        Switch("verbose", short="v", type=float, value=3.0),
        Switch("no_general_overview", short="g", type=bool, value=False),
        Switch("no_key_concepts", short="k", type=bool, value=False),
        Switch("no_section_by_section", short="s", type=bool, value=False),
        Switch("no_additional_information", short="a", type=bool, value=False),
        Switch("no_helpful_vocabulary", short="he", type=bool, value=False),
        Switch("no_explain_to_5th_grader", short="e", type=bool, value=False),
        Switch("no_conclusion", short="c", type=bool, value=False),
        Switch("env", short="e", type=str, value=None)
    ]

    if len(sys.argv) < 3:
        print("Usage: python script.py [options] <input> <output>")
        sys.exit(1)

    process_path(sys.argv[-2], sys.argv[-1], my_switches)
