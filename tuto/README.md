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

### CH02 : Traitements basiques du text

- [CoreNLP](CH02/preprocessing_java_CoreNLP.ipynb) : Java
  - I. Preprocessing pipeline
  - II. Get tokens
  - III. Get sentences
  - IV. Get lemmas
  - V. Other languages
- [LangPi](CH02/preprocessing_java_LangPi.ipynb) : Java
  - I. Text normalization
  - II. Text segmentation
  - III. StopWords Filtering
  - IV. Text stemming
- [NLTK](CH02/preprocessing_python_NLTK.ipynb) : Python
  - I. Text tokenization (sentences, words)
  - II. StopWords filtering
  - III. Stemming
  - IV. Lemmatization
  - V. Distance
- [OpenNLP](CH02/preprocessing_java_OpenNLP.ipynb) : Java
  - I. Language detection (detection, training)
  - II. Sentence boundary detection (detection, training)
  - III. Word tokenization
  - TODO: compléter
- [Spacy](CH02/preprocessing_python_Spacy.ipynb) : Python
  - I. Sentence tokenization
  - II. Words tokenization
  - III. StopWords filtering
  - IV. Lemmatization

### CH03 : Modèles de langage

- [Keras(TensorFlow)](CH03/models_python_Keras.ipynb) : Python
  - I. Simple FeedForward 3-gram model (with, without embedding)
  - II. LSTM model
- [NLTK](CH03/models_python_NLTK.ipynb) : Python
  - I. Preprocessing (padding)
  - II. NGrams
  - III. Vocabulary
  - IV. Language models (MLE, Smoothed models)
  
### CH04 : Étiquetage morpho-syntaxique (Étiquetage de séquences)

- [flair](CH04/sequences_python_flair.ipynb) : Python
  - I. Data preparation: Tokenization, Corpus preparation
  - II. Part of Speech (PoS) tagging: Tagging, Training
  - II. Named Entity Recognition (NER): Recognition, Training
- [NLTK](CH04/sequences_python_NLTK.ipynb) : Python
  - I. Part of Speech (PoS) tagging: Default, RegEx, CRF, HMM, PerceptronTagger, Brill
  - II. Named Entity Recognition (NER): default, training
  - III. Chunking

### CH05 : Analyse syntaxique

- [CoreNLP](CH05/parsing_java_CoreNLP.ipynb) : Java
  - I. Constituancy Parsing
  - II. Dependency Parsing %TODO complete
- [NLTK](CH05/parsing_python_NLTK.ipynb) : Python
  - I. Grammars: Context Free grammar (CFG), Loading grammars, Treebanks
  - II. Parsing: Recursive Descent, Shift-Reduce, Chart
  - III. Generation

### CH06 : Sémantique lexicale

- [Gensim](CH06/encoding_python_gensim.ipynb) : Python
  - I. TF-IDF
  - II. LSA
  - III. LDA
  - IV. Word2Vec
  - V. Fasttext
- [NLTK](CH06/encoding_python_NLTK.ipynb) : Python
  - I. WordNet: Synsets, SynSet properties and relations, Lemma relations
  - II. Operations: Lowest Common Hypernyms, Similarity
  - III. Multilingual WordNet
- [Scikit-learn](CH06/encoding_python_sklearn.ipynb) : Python
  - I. Vectorization: TF, IDF, LSA
  - II. Parameters: reading, preprocessing
  - III. Similarity
- [TensorFlow-BERT](CH06/encoding_python_TF_BERT.ipynb) : Python
  - I. Text preprocessing: Using tf-hub model, Training a preprocessor
  - II. Text encoding: Using tf-hub model, Train a model from scratch
  - III. Fine-Tuning
- [TensorFlow-ELMo](CH06/encoding_python_TF_ELMo.ipynb) : Python
  - I. Text encoding
  - II. Using ELMo

### CH07 : Sémantique de la phrase

- [NLTK](CH07/sentsem_python_NLTK.ipynb) : Python
  - I. Thematic relations: FrameNet, PropBank, VerbNet
  - II. Propositional Logic: Expressions and proves, First-Order Logic (FOL)
  - III. Semantic parsing: λ-Calculus, analysis