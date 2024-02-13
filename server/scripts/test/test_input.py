import pytest
from langchain.document_loaders import YoutubeLoader, UnstructuredPowerPointLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import FAISS
from langchain.chat_models import ChatOpenAI
from langchain.chains import LLMChain
from dotenv import find_dotenv, load_dotenv
from langchain.prompts.chat import (
    ChatPromptTemplate,
    SystemMessagePromptTemplate,
)
from unstructured.partition import pptx

from main import *

load_dotenv(find_dotenv())
embeddings = OpenAIEmbeddings()


def test_valid_input():
    video_url = "https://www.youtube.com/watch?v=CtsRRUddV2s"
    result = create_db_from_youtube_video_url(video_url)
    assert result is not None


def test_invalid_youtube_url():
    video_url = "invalid_url"
    with pytest.raises(ValueError):
        create_db_from_youtube_video_url(video_url)


def test_large_transcript():
    video_url = "https://www.youtube.com/watch?v=8jLOx1hD3_o"
    result = create_db_from_youtube_video_url(video_url)
    assert result is not None


def test_create_db_from_pptx_valid_input():
    pptx_path = "Functions_in_Python.pptx"
    result = create_db_from_pptx(pptx_path)
    assert result is not None


def test_create_db_from_pdf_valid_input():
    pdf_path = "linear_regression.pdf"
    result = create_db_from_pdf(pdf_path)
    assert result is not None


def test_create_db_from_pdf_invalid_path():
    pdf_path = "invalid_path.pdf"
    with pytest.raises(ValueError):  # Adjust the exception type accordingly
        create_db_from_pdf(pdf_path)


def test_create_db_from_pdf_empty_content():
    pdf_path = "blank.pdf"
    result = create_db_from_pdf(pdf_path)
    assert result == []  # Assuming an empty list is returned for an empty document


def test_create_db_from_pdf_large_content():
    pdf_path = "1MB.pdf"
    result = create_db_from_pdf(pdf_path)
    assert result is not None
