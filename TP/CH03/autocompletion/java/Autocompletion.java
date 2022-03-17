import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.lang.Math;
import java.util.stream.Collectors;

public class Autocompletion {
	private LaplaceBiGram modele = new LaplaceBiGram();
	private List<Tuple<String, String>> eval = new ArrayList<>();

	public void entrainer(String url){
		try {
			BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(url), "UTF-8"));
			List<String[]> data = new ArrayList<>();
			String l;
			while((l = f.readLine()) != null) {
				String[] phrase = l.toLowerCase().split(" ");
				if (phrase.length > 0) data.add(phrase);
			}
			this.modele.entrainer(data);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public List<Tuple<String, Double>> estimer(String phrase, int nbr){
		List<Tuple<String, Double>> motsScores;
		motsScores = this.modele.estimer(phrase.toLowerCase().split(" "));
		return motsScores.subList(0, nbr);
	}

	public void charger_modele(String url){
		try {
			FileInputStream f = new FileInputStream(url);
			ObjectInputStream in = new ObjectInputStream(f);
			modele = (LaplaceBiGram) in.readObject();
			in.close();
			f.close();
		} catch(IOException|ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void sauvegarder_modele(String url){
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

	public void charger_evaluation(String url){
		try {
			BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(url), "UTF-8"));
			String l;
			while((l = f.readLine()) != null) {
				String[] info = l.toLowerCase().split("	");
				if (info.length > 1) this.eval.add(new Tuple<String, String>(info[0], info[1]));
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public double evaluer(int n, int m){//Mean reciprocal rank
		if (n == -1) n = this.eval.size();
		else Collections.shuffle(this.eval);
		double score = 0.0;
		for (int j=0; j<n; j++){
			Tuple<String, String> test = eval.get(j);
			System.out.println("p(" + test.v + "|" + test.k + ")");
			List<Tuple<String, Double>> res = estimer(test.k, m);
			System.out.println("found: " + res);
			double i = res.stream().map(e -> e.k).collect(Collectors.toList()).indexOf(test.v) + 1;
			if (i > 0) score += 1/i;
		}
		score /= n;
		System.out.println("Score = " + score);
		return score;
  }

	public static void main(String[] args) {
		Autocompletion program = new Autocompletion();
		program.entrainer("data/algerie_train.txt");
		program.sauvegarder_modele("./t_java.mdl");
// 		String phrase = "L' Algérie faisant partie du";
// 		List<Tuple<String, Double>> res = program.estimer(phrase, 10);
// 		System.out.println(res);
		program.charger_evaluation("data/algerie_test.txt");
// 		80 exemplaires ; 10 résultats possibles
		double mrr = program.evaluer(80, 10);
		//System.out.println(mrr);
	}
}

class LaplaceBiGram implements Serializable {
	private Map<String, Integer> uniGrams = new HashMap<>();
	private Map<String, Integer> biGrams = new HashMap<>();

	// TODO : compléter cette méthode
	public void entrainer(List<String[]> data){
		uniGrams.put("<s>", 0);
	}//entrainer

	// TODO : compléter cette méthode
	public double noter(String past, String current){
		return  0.0;
	}

	// TODO : compléter cette méthode
	public List<Tuple<String, Double>> estimer(String[] mots){
		List<Tuple<String, Double>> motsScores = new ArrayList<>();
		// ajouter des tuples (mot, score) à cette liste

		Collections.sort(motsScores, new Comparator<Tuple<String, Double>>() {
			@Override
			public int compare(Tuple<String, Double> o1, Tuple<String, Double> o2) {
				double sub = o2.v - o1.v;
				return (int) Math.signum(sub);
			}
		});
		return motsScores;
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
