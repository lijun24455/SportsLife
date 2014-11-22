package sysu.project.lee.sportslife.Excercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseManager {
	private List<Exercise> exercises;

	public ExerciseManager() {
		// TODO Auto-generated constructor stub
		exercises = new ArrayList<Exercise>();
	}

	public void addOneExercise(Exercise exer) {
		exercises.add(exer);
	}

	public void deleteOneExercise(Exercise exer) {
		exercises.remove(exer);
	}

	public void showOneExercise(Exercise exer) {

	}

	public void modifyOneExercise(Exercise exer) {

	}

	public void makeNewExercise(Exercise exer) {

	}

	public List<Exercise> getExercisesList() {
		return exercises;
	}
}
