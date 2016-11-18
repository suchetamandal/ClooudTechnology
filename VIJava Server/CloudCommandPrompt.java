import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is build to provide customerized command line prompt to user
 * @author Sucheta Mandal
 *
 */
public class CloudCommandPrompt {
	BufferedReader commandReader;
	String[] commands = new String[3];

	void intialiseCommandPrompt() {
		System.out.print("sucheta-486>");
		BufferedReader commandReaderNew = new BufferedReader(new InputStreamReader(System.in));
		this.commandReader = commandReaderNew;
	}

	public String[] getCommands() {
		String readString = null;
		commands[0] = "";
		commands[1] = "";
		commands[2] = "";
		int commandCount = 0;
		StringBuilder command = new StringBuilder();
		try {
			readString = commandReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (readString.length() == 0) {
			return commands;
		} else if (readString.length() == 4 || readString.length() == 2) {
			commands[commandCount] = readString.toString();
			return commands;
		} else {
			for (int i = 0; i < readString.length(); i++) {
				if (!Character.isWhitespace(readString.charAt(i)) || commandCount == 2) {
					command.append(readString.charAt(i));
				} else {
					commands[commandCount] = command.toString();
					command = new StringBuilder();
					commandCount++;
				}
			}
			commands[commandCount] = command.toString();
		}

		return commands;
	}

}
