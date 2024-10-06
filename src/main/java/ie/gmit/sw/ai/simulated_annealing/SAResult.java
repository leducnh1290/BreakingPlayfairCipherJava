package ie.gmit.sw.ai.simulated_annealing;

import ie.gmit.sw.ai.WordSeparator;

public class SAResult {
	
	private String bestKey;
	private String textResult;
	private double bestScore;
	
	public SAResult(String bestKey, String textResult, double bestScore) {
		this.bestKey = bestKey;
		this.textResult = textResult;
		this.bestScore = bestScore;
	}
	
	public String toString() {
		WordSeparator ws = new WordSeparator();
		ws.loadDictionary("C:\\Users\\leduc\\IdeaProjects\\demo1\\PlayfairCipherBreaker\\src\\main\\java\\ie\\gmit\\sw\\ai\\dic.txt", 0, 1);

		return String.format("Best Key: %s With Score: %.5f\n"
				+ "Decrypted Message: %s\n",
				bestKey, bestScore,ws.segment(textResult).first);
	}

	public String getBestKey() {
		return bestKey;
	}

	public void setBestKey(String bestKey) {
		this.bestKey = bestKey;
	}

	public String getTextResult() {
		return textResult;
	}

	public void setTextResult(String textResult) {
		this.textResult = textResult;
	}

	public double getBestScore() {
		return bestScore;
	}

	public void setBestScore(double bestScore) {
		this.bestScore = bestScore;
	}	
}
