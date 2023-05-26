/*	 Copyright 2016 Hamoon Mousavi
 *
 * 	 This file is part of Walnut.
 *
 *   Walnut is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Walnut is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Walnut.  If not, see <http://www.gnu.org/licenses/>.
*/

package Main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import Automata.Automaton;
import Token.Token;

/**
 *
 */
public class Computer {
	Predicate predicate_object;
	String predicate_string;
	Expression result;
	Automaton D;
	StringBuffer log;
	StringBuffer log_details;
	String mpl;
	boolean printSteps;
	boolean printDetails;
	public Computer(String predicate, boolean printSteps, boolean printDetails) throws Exception {
		this.log = new StringBuffer();
		this.log_details = new StringBuffer();
		mpl = "";
		this.predicate_string = predicate;
		predicate_object = new Predicate(predicate);
		this.printSteps = printSteps;
		this.printDetails = printDetails;
		compute();
	}

	public Computer(String predicate, boolean printSteps, boolean printDetails, boolean Debug) throws Exception {
		this.log = new StringBuffer();
		this.log_details = new StringBuffer();
		mpl = "";
		this.predicate_string = predicate;
		predicate_object = new Predicate(predicate);
		this.printSteps = printSteps;
		this.printDetails = printDetails;
		computeDebug();
	}

	public Automaton getTheFinalResult(){
		return result.M;
	}

	public void writeMatrices(String address, List<String> free_variables) throws Exception{
		try {
			mpl = result.M.write_matrices(address, free_variables);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	public void writeLog(String address) throws Exception {
		PrintWriter out;
		try {
			out = new PrintWriter(address, "UTF-8");
			out.write(log.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	public void writeDetailedLog(String address) throws Exception{
		PrintWriter out;
		try {
			out = new PrintWriter(address, "UTF-8");
			out.write(log_details.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	public void drawAutomaton(String address) throws Exception{
		result.M.draw(address, predicate_string, false);
	}
	public void write(String address){
		result.M.write(address);
	}
	public String toString(){
		return result.toString();
	}
	private void compute() throws Exception {
		Stack<Expression> expression_Stack = new Stack<Expression>();
		List<Token> postOrder = predicate_object.get_postOrder();
		String prefix = "";
		long timeBeginning = System.currentTimeMillis();
		String step,preStep;
		for(Token t:postOrder) {
			try{
				long timeBefore = System.currentTimeMillis();
				String operands = "";
				t.act(expression_Stack, printDetails, prefix, log_details);
				long timeAfter = System.currentTimeMillis();
				if(t.isOperator() && expression_Stack.peek().is(Type.automaton)) {
					step = prefix + expression_Stack.peek() + ":" +
						expression_Stack.peek().M.Q + " states - " + (timeAfter-timeBefore) + "ms";
					log.append(step + UtilityMethods.newLine());
					log_details.append(step + UtilityMethods.newLine());
					if(printSteps || printDetails) {
						System.out.println(step);
					}

					prefix += " ";
				}
			} catch(Exception e) {
				e.printStackTrace();
				String message = e.getMessage();
				message += UtilityMethods.newLine() + "\t: char at " + t.getPositionInPredicate();
				throw new Exception(message);
			}
		}

		long timeEnd = System.currentTimeMillis();
		step = "Total computation time: " + (timeEnd - timeBeginning) + "ms.";
		log.append(step);
		log_details.append(step);
		if(printSteps||printDetails) {
			System.out.println(step);
		}

		if(expression_Stack.size() > 1) {
			String message =
				"Cannot evaluate the following into a single automaton:" +
				UtilityMethods.newLine();
			Stack<Expression> tmp = new Stack<Expression>();

			while(!expression_Stack.isEmpty()) {
				tmp.push(expression_Stack.pop());
			}

			while(!tmp.isEmpty()) {
				message += tmp.pop() + UtilityMethods.newLine();
			}

			message += "Probably some operators are missing.";
			throw new Exception(message);
		} else if(expression_Stack.isEmpty()) {
			throw new Exception("Evaluation ended in no result.");
		} else if(expression_Stack.size() == 1) {
			result = expression_Stack.pop();
			if(!result.is(Type.automaton)){
				throw new Exception("The final result of the evaluation is not of type " + Type.automaton);
			}
		}
	}

	private void computeDebug() throws Exception {
		Stack<Expression> expression_Stack = new Stack<Expression>();
		List<Token> postOrder = predicate_object.get_postOrder();
		String prefix = "";
		long timeBeginning = System.currentTimeMillis();
		String step,preStep;

		boolean auto = false;

		Scanner scanner2 = new Scanner(System.in);
		System.out.println("Debug automatique O/N :");

		String automatique = scanner2.nextLine();

		switch (automatique){
			case "o":
			case "O":
				auto = true;
				System.out.println("postfixée : " + postOrder);
				break;
			default:
				auto = false;
				break;
		}

		for(Token t:postOrder) {
			try{

				boolean boucle = true;

				long timeBefore = System.currentTimeMillis();
				String operands = "";
				t.act(expression_Stack, printDetails, prefix, log_details);
				long timeAfter = System.currentTimeMillis();
				if(t.isOperator() && expression_Stack.peek().is(Type.automaton)) {
					step = prefix + expression_Stack.peek() + ":" +
							expression_Stack.peek().M.Q + " states - " + (timeAfter-timeBefore) + "ms";
					log.append(step + UtilityMethods.newLine());
					log_details.append(step + UtilityMethods.newLine());
					if(printSteps || printDetails) {
						System.out.println(step);
					}

					prefix += " ";
				}

				if(auto) {
					System.out.println("Lecture du token : " + t.toString() + " du type : " + t.getClass().getName().split("\\.")[1]);
					System.out.println("expression : " + expression_Stack);
					TimeUnit.SECONDS.sleep(2);
				}

				else {
					while (boucle) {
						Scanner scanner = new Scanner(System.in);
						System.out.println("Lecture du token : " + t.toString() + " du type : " + t.getClass().getName().split("\\.")[1]);
						System.out.println("1 : Afficher l'état de la pile d'expression \n" +
								"2 : Afficher la formule en notation postfixée \n" +
								"3 : Afficher les deux \n" +
								"4 : Poursuivre le calcul");

						String entree = scanner.nextLine();

						switch (entree) {
							case ("1") -> System.out.println("expression : " + expression_Stack);
							case ("2") -> System.out.println("postfixée : " + postOrder);
							case ("4") -> boucle = false;
							default -> {
								System.out.println("postfixée : " + postOrder);
								System.out.println("expression : " + expression_Stack);
							}
						}
					}
				}

			} catch(Exception e) {
				e.printStackTrace();
				String message = e.getMessage();
				message += UtilityMethods.newLine() + "\t: char at " + t.getPositionInPredicate();
				throw new Exception(message);
			}
		}

		long timeEnd = System.currentTimeMillis();
		step = "Total computation time: " + (timeEnd - timeBeginning) + "ms.";
		log.append(step);
		log_details.append(step);
		if(printSteps||printDetails) {
			System.out.println(step);
		}

		if(expression_Stack.size() > 1) {
			String message =
					"Cannot evaluate the following into a single automaton:" +
							UtilityMethods.newLine();
			Stack<Expression> tmp = new Stack<Expression>();

			while(!expression_Stack.isEmpty()) {
				tmp.push(expression_Stack.pop());
			}

			while(!tmp.isEmpty()) {
				message += tmp.pop() + UtilityMethods.newLine();
			}

			message += "Probably some operators are missing.";
			throw new Exception(message);
		} else if(expression_Stack.isEmpty()) {
			throw new Exception("Evaluation ended in no result.");
		} else if(expression_Stack.size() == 1) {
			result = expression_Stack.pop();
			if(!result.is(Type.automaton)){
				throw new Exception("The final result of the evaluation is not of type " + Type.automaton);
			}
		}
	}
}
