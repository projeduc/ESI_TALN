# Outils de traitement automatique du langage naturel

Ici, on va présenter quelques outils du TALN.

## List de quelques outils

|Outil|Lien|Demo|Langage|Tâches|
|---|---|---|---|---|
|AllenNLP|https://allennlp.org/|https://demo.allennlp.org/|Python| |
|Apache OpenNLP|https://opennlp.apache.org/| |Java| |
|Apache Tika|https://tika.apache.org/| |Java|Text extraction|
|BERT|https://github.com/google-research/bert| |Python|Modele|
|Bling Fire|https://github.com/microsoft/BlingFire| |Multiple (C++)|Preprocessing|
|ERNIE|https://github.com/thunlp/ERNIE| |Python|Modle|
|FastText|https://fasttext.cc/| |Python|Text classification and word representation|
|FLAIR|https://github.com/flairNLP/flair| |Python|NER, PoS|
|TextBlob|https://github.com/sloria/textblob| |Python| |
|Gensim|https://radimrehurek.com/gensim/| |Python|Topic modeling|
|MS IceCaps|https://github.com/microsoft/icecaps| |Python|Conversation agent|
|GPT-2|https://github.com/openai/gpt-2| |Python|Modele|
|jiant|https://github.com/nyu-mll/jiant| |Python|Research tasks|
|NeuralCoref|https://github.com/huggingface/neuralcoref| |Python(Spacy)|CoRef|
|NLP Architect|https://github.com/IntelLabs/nlp-architect| |Python| |
|NLTK|https://www.nltk.org/| |Python| |
|spaCy|https://spacy.io/| |Python| |
|Stanford CoreNLP|https://stanfordnlp.github.io/CoreNLP/|https://corenlp.run/|Java| |
|Texar-pytorch|https://github.com/asyml/texar-pytorch| |Python| |
|TextBlob|https://github.com/sloria/TextBlob| |Python| |
|pattern|https://github.com/clips/pattern| |Python| |

## Tutoriels

Ici, on utile [Jupyter Notebook](https://jupyter.org/).
On général, il est destiné pour la documentation en Python.
Mais, il existe des noyaux pour documenter d'autres langages de programmation.
Pour Java, on utilise [Ganymede](https://github.com/allen-ball/ganymede).

### CH2: Traitements basiques du text

- [CoreNLP](./CH2/preprocessing_java_CoreNLP.ipynb) : Java
  -  TODO: compléter
- [LangPi](CH2/preprocessing_java_LangPi.ipynb) : Java
  - I. Text normalization
  - II. Sentence segmentation
  - III. Words tokenization
  - IV. Text stemming
- [OpenNLP](CH2/preprocessing_java_OpenNLP.ipynb) : Java
  - I. Language detection (detection, training)
  - II. Sentence boundary detection (detection, training)
  - III. Word tokenization
  - TODO: compléter
- [NLTK](CH2/preprocessing_python_NLTK.ipynb) : Python
  - I. Text tokenization (sentences, words)
  - II. StopWords filtering
  - III. Stemming
  - IV. Lemmatization
  - V. Distance
- [Spacy](CH2/preprocessing_python_Spacy.ipynb) : Python
  - I. Sentence tokenization
  - II. Words tokenization
  - III. StopWords filtering
  - IV. Lemmatization
