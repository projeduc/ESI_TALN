from nltk.corpus import treebank_chunk


sents = []

for sent in treebank_chunk.tagged_sents(tagset='universal'):
    s = ''
    for word_tag in sent:
        word, tag = word_tag
        s += word + '/' + tag + ' '
    sents.append(s + '\n')

N = len(sents)
T = int(N * 0.26)

f = open('train_data.txt', 'w')
for i in range(0, N-T):
    f.write(sents[i])
f.close()

f = open('test_data.txt', 'w')
for i in range(N-T, N):
    f.write(sents[i])
f.close()
