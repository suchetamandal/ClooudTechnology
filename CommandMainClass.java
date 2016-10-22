import java.net.URL;

import com.vmware.vim25.mo.ServiceInstance;

/**
 * Main class for starting connection and command prompt
 * 
 * @author Sucheta Mandal
 *
 */
public class CommandMainClass {

	public static void main(String[] args) throws Exception {
		CloudCommandPrompt userPrompt = new CloudCommandPrompt();
		/** Uncomment this to run directly from Eclpise
		 */
		//Scanner scanner = new Scanner(System.in);
		//String url = scanner.next();
		//String loginId = scanner.next();
		//String password = scanner.next();
		
		//ServiceInstance serviceInstance = new ServiceInstance(new URL(url),
		//		loginId, password, true);
		
		ServiceInstance serviceInstance = new ServiceInstance(new URL(args[0]),
				args[1], args[2], true);
		CommandEngine commandEngine = new CommandEngine(userPrompt,serviceInstance);
		System.out.println("CMPE 281 HW2 from Sucheta Mandal");
		commandEngine.runCommand();
	}

}