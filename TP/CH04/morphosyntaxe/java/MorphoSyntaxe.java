import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.lang.Math;
import java.util.stream.*;
import java.util.Arrays;

public class MorphoSyntaxe {
	public static enum OPT {FORCE_BRUTE, VITERBI, GOURMAND}
	private HMM modele;
	private List<Tuple<List<String>, List<String>>> eval = new ArrayList<>();

	public void entrainer(String url){
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			List<List<Tuple<String, String>>> data = new ArrayList<>();
			Set<String> tagsList = new LinkedHashSet<String>();
			String l;
			while((l = f.readLine()) != null) {
				String[] phrase = l.trim().toLowerCase().replace("\\/", "@@").split(" ");
				if (phrase.length > 0){
					List<Tuple<String, String>> p = new ArrayList<>();
					for (String mottag: phrase){
						String[] mt = mottag.split("/");
						p.add(new Tuple<String, String>(mt[0].replace("@@", "/"), mt[1]));
						tagsList.add(mt[1]);
					}
					data.add(p);
				}
			}
			this.modele = new HMM(new ArrayList<String>(tagsList));
			this.modele.entrainer(data);
			f.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private List<String> estimer(List<String> phrase, OPT opt){
		List<String> resTags;
		switch(opt){
			case VITERBI:
			resTags = this.modele.estimerViterbi(phrase);
			break;
			case GOURMAND:
			resTags = this.modele.estimerGourmand(phrase);
			break;
			default:
			resTags = this.modele.estimerForceBrute(phrase);
		}
		return resTags;

		// return IntStream.range(0, resTags.size())
		// 	.mapToObj(i -> new Tuple<String, String>(phrase[i], resTags.get(i)))
		// 	.collect(Collectors.toList());
	}

	public List<String> estimer(String phrase, OPT opt){
		return estimer(Arrays.asList(phrase.trim().toLowerCase().split(" ")), opt);
	}

	public void chargerModele(String url){
		try {
			FileInputStream f = new FileInputStream(url);
			ObjectInputStream in = new ObjectInputStream(f);
			modele = (HMM) in.readObject();
			in.close();
			f.close();
		} catch(IOException|ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void sauvegarderModele(String url){
		try {
			FileOutputStream f = new FileOutputStream(url);
			ObjectOutputStream out = new ObjectOutputStream(f);
			out.writeObject(modele);
			out.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void chargerEvaluation(String url){
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			String l;
			while((l = f.readLine()) != null) {
				String[] mottags = l.trim().toLowerCase().replace("\\/", "@@").split(" ");
				if (mottags.length > 1){
					List<String> mots = new ArrayList<String>();
					List<String> tags = new ArrayList<String>();
					for (String mottag: mottags){
						String[] mt = mottag.split("/");
						mots.add(mt[0].replace("@@", "/"));
						tags.add(mt[1]);
					}
					this.eval.add(new Tuple<List<String>, List<String>>(mots, tags));
				}
			}
			f.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public double evaluerPhrase(List<String> sysTags, List<String> refTags){
		double INT = 0.0;
		for (int i=0; i< sysTags.size(); i++){
			if (sysTags.get(i).equals(refTags.get(i))) INT++;
		}
		return INT/refTags.size();
	}

	public void evaluer(int n){
		if (n==-1) n = this.eval.size();
		else Collections.shuffle(this.eval);
		double scoreGourmand = 0.0;
        double scoreViterbi = 0.0;
        double scoreForceBrute = 0.0;
		Tuple<List<String>, List<String>> test;
		List<String> sysTags;
		double score;
		for (int i=0; i<n; i++){
			test = this.eval.get(i);
			System.out.println("=======================");
			System.out.println("phrase : " + test.k);
			System.out.println("tags de référence : " + test.v);

			// sysTags = estimer(test.k, OPT.FORCE_BRUTE);
			// System.out.println("brute force : " + sysTags);
			// score = evaluerPhrase(sysTags, test.v);
			// System.out.println("score : " + score);
			// scoreForceBrute += score;

			sysTags = estimer(test.k, OPT.VITERBI);
			System.out.println("viterbi : " + sysTags);
			score = evaluerPhrase(sysTags, test.v);
			System.out.println("score : " + score);
			scoreViterbi += score;

			sysTags = estimer(test.k, OPT.GOURMAND);
			System.out.println("gourmand : " + sysTags);
			score = evaluerPhrase(sysTags, test.v);
			System.out.println("score : " + score);
			scoreGourmand += score;
		}
		scoreGourmand /= n;
		scoreViterbi /= n;
		scoreForceBrute /= n;
		System.out.println("----------- Scores totaux -------------");
		System.out.println("score gourmand : " + scoreGourmand);
		System.out.println("score viterbi : " + scoreViterbi);
		System.out.println("score force brute : " + scoreForceBrute);
	}


	// TESTS
	//=======================
	public static void tester_noter(){
		MorphoSyntaxe annotateur = new MorphoSyntaxe();
		annotateur.entrainer("./data/test.txt");
		String phrase = "il peut aider";
		
		String[] tags = new String[]{"det", "verb", "verb"};
		double[] scores = new double[]{-3.465735902799726, -3.401197381662155, -3.3141860046725258};
		String[] mots = phrase.toLowerCase().split(" ");
		String past_tag="";
		for (int i=0; i<mots.length; i++){
			System.out.print("mot=" + mots[i] + ", tag=" + tags[i] + ", score=" + scores[i]);
			System.out.println(", mon score =" + annotateur.modele.noter(past_tag, tags[i], mots[i]));
			past_tag = tags[i];
		}

	}

	public static void tester_viterbi(){
		MorphoSyntaxe annotateur = new MorphoSyntaxe();
		annotateur.entrainer("./data/univ_train.txt");
		String phrase = "this has some logic .";
		
		System.out.println("viterbi doit être comme force brute");
		System.out.println("force brute : " + annotateur.estimer(phrase, OPT.FORCE_BRUTE));
		System.out.println("gourmand : " + annotateur.estimer(phrase, OPT.GOURMAND));
		System.out.println("viterbi : " + annotateur.estimer(phrase, OPT.VITERBI));
	}

	public static void evaluation(){
		MorphoSyntaxe annotateur = new MorphoSyntaxe();
		annotateur.entrainer("./data/univ_train.txt");
		annotateur.chargerEvaluation("./data/univ_test.txt");
		annotateur.evaluer(-1);
	}

	public static void main(String[] args) {
		tester_noter();
		//tester_viterbi();
		//evaluation();
	}
}

class HMM implements Serializable {
	// Tag_i --> tag_i+1 --> prob // probabilités de transition
	private Map<String, Map<String, Double>> transition = new HashMap<>();
	// Tag --> mot --> prob // probabilité d'émission
	private Map<String, Map<String, Double>> emission = new HashMap<>();
	// Tag --> prob // probabilité initiale des états
	private Map<String, Double> init = new HashMap<>();
	private List<String> tags;
	// score des mots non trouvées dans l'emission
	// unk_score = 1/|V|
	private double unkScore = 0.0;

	public void printEmission(){
		System.out.println(emission);
	}

	public HMM(List<String> tags){
		this.tags = tags;
		for (String tag: tags){
			transition.put(tag, new HashMap<String, Double>());
			emission.put(tag, new HashMap<String, Double>());
			init.put(tag, 0.0);
			for (String tagSuiv: tags){
				this.transition.get(tag).put(tagSuiv, 0.0);
			}
		}
	}

	public static void initInc(Map<String, Double> map, String cle, double ...val){
		double initVal = val.length > 0 ? val[0] : 1.0;
		double incVal = val.length > 1 ? val[1] : 1.0;
		if (map.containsKey(cle)) map.put(cle, map.get(cle)+ incVal);
		else map.put(cle, initVal);
	}

	public void entrainer(List<List<Tuple<String, String>>> data){
		Map<String, Double> tagsFreq = new HashMap<>();
		Set<String> vocab = new HashSet<>();

		for (List<Tuple<String, String>> phrase : data){
			Tuple<String, String> mottagPrec = phrase.get(0);
			vocab.add(mottagPrec.k);
			initInc(this.init, mottagPrec.v);
			initInc(tagsFreq, mottagPrec.v);
			if (! this.emission.containsKey(mottagPrec.v)){
				this.emission.put(mottagPrec.v, new HashMap<String, Double>());
			}
			initInc(this.emission.get(mottagPrec.v), mottagPrec.k);
			for (int j=1; j< phrase.size(); j++){
				Tuple<String, String> mottag = phrase.get(j);
				vocab.add(mottag.k);
				// transition tag_prec --> tag
				initInc(this.transition.get(mottagPrec.v), mottag.v);
				// émission tag --> mot
				initInc(this.emission.get(mottag.v), mottag.k);
				initInc(tagsFreq, mottag.v);
 				mottagPrec = mottag;
			}
		}

		int V = vocab.size();
		this.unkScore = 1.0/V;
		int Vt = this.tags.size();
		int nbrPhrases = data.size();
		for (String tag: this.tags){
			this.init.put(tag, (this.init.get(tag)+1)/(nbrPhrases+Vt));
			double nbrTag = tagsFreq.get(tag);
			Map<String, Double> emissionTag = this.emission.get(tag);
			for (String mot: emissionTag.keySet()){
				emissionTag.put(mot, (emissionTag.get(mot)+1.0)/(nbrTag + V));
			}
			Map<String, Double> transitionTag = this.transition.get(tag);
			for (String tagSuiv: this.tags){
				transitionTag.put(tagSuiv, (transitionTag.get(tagSuiv)+1.0)/(nbrTag+Vt));
			}
		}
		//////////////////////
	}//entrainer

	//TODO: compléter cette méthode
	public double noter(String pastPos, String currentPos, String mot){
		return Math.log(1);
	}

	public List<String> estimerForceBrute(List<String> phrase){
		List<String> tagsMax = new ArrayList<String>();
		double scoreMax = Double.NEGATIVE_INFINITY;
		LinkedList<Tuple<String,Tuple<Integer,Double>>> pile = new LinkedList<>();
		pile.addLast(new Tuple<String,Tuple<Integer,Double>>("", new Tuple<Integer,Double>(0, 0.0)));
		int T = phrase.size();
		int N = tags.size();
		while (pile.size() > 0){
			int i = pile.size() - 1;
			Tuple<String,Tuple<Integer,Double>> triple = pile.pollLast();
			String pastPos = triple.k;
			int t = triple.v.k;
			double score = triple.v.v;
			pile.addLast(new Tuple<String,Tuple<Integer,Double>>(pastPos, new Tuple<Integer,Double>(t + 1, score)));

			//forward
			while (i < T){
				String currentPos = tags.get(t);
				String mot = phrase.get(i);
				score += noter(pastPos, currentPos, mot);
				pile.addLast(new Tuple<String,Tuple<Integer,Double>>(currentPos, new Tuple<Integer,Double>(1, score)));
				i++;
				t = 0;
				pastPos = currentPos;
			}

			if (scoreMax < score){
				scoreMax = score;
				tagsMax = pile.stream().map(e -> e.k).collect(Collectors.toList());
			}

			//backward
			pile.pollLast(); //dépiler le dernier élément
			while ((pile.size() > 0) && (pile.getLast().v.k == N)) pile.pollLast();
		}
		
		tagsMax.remove(0);

		return tagsMax;
	}

	public List<String> estimerGourmand(List<String> phrase){
		List<String> tags = new ArrayList<String>();
		String pastTag = "";
		for (String mot: phrase){
			double maxScore = Double.NEGATIVE_INFINITY;
			String maxTag = "";
			for (String tag: this.tags){
				double score = this.noter(pastTag, tag, mot);
				if (score > maxScore){
					maxScore = score;
					maxTag = tag;
				}
			}
			tags.add(maxTag);
			pastTag = maxTag;
		}
		return tags;
	}

	//TODO: compléter cette méthode : vous devez suivre la même structure définie ici
	public List<String> estimerViterbi(List<String> phrase){
		int N = tags.size();
		int T = phrase.size();
		double [][] viterbi = new double[N][T];
		int [][] backpointer = new int[N][T];
		

		List<String> resTags = new ArrayList<String>();

		
		return resTags;
	}

}

class Tuple<K, V> {
	public final K k;
	public final V v;
	public Tuple(K k, V v) {
		this.k = k;
		this.v = v;
	}

	public String toString(){
		return "(" + k + ", " + v + ")";
	}
}
