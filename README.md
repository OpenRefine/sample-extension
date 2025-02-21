AI Extension for OpenRefine 
===========================

The OpenRefine AI Extension bridges the power of modern language models with OpenRefine's robust data transformation capabilities. This extension enables users to leverage any LLM provider that supports a chat completion API endpoint, bringing AI-powered data wrangling, enhancement, and analysis directly into your OpenRefine workflows. For more information, read the [AI Column Extraction](llm-prompt-guide.md) and [Provider Setup](llm-provider-guide.md) guides in this repo.

## Purpose
The extension serves multiple purposes in the data processing pipeline:

1. Intelligent Data Cleaning: Use LLMs to suggest and implement context-aware data cleaning operations that go beyond rule-based approaches.
2. Semantic Enrichment: Enhance datasets by generating additional attributes or metadata based on existing content.
3. Natural Language Transformations: Express complex data transformations in plain English.
4. Anomaly Detection: Identify unusual patterns or outliers in your data through AI-powered analysis.
5. Content Generation: Fill gaps in your datasets with contextually appropriate synthetic data.

It works with **OpenRefine 3.8.7 and later versions of OpenRefine**.


### Install this extension in OpenRefine

Download the .zip file of the [latest release of this extension](https://github.com/sunilnatraj/llm-extension/releases).
Unzip this file and place the unzipped folder in your OpenRefine extensions folder. [Read more about installing extensions in OpenRefine's user manual](https://docs.openrefine.org/manual/installing#installing-extensions).

When this extension is installed correctly, when you open a project you will see in the Extensions bar AI menu.