import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * This class is responsible for running commands entered in consol
 * 
 * @author Sucheta Mandal
 *
 */
public class CommandEngine {

	CloudCommandPrompt userPrompt;
	ServiceInstance serviceInstance;
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	public CommandEngine(CloudCommandPrompt value, ServiceInstance serviceInstance) throws Exception {
		this.userPrompt = value;
		this.serviceInstance = serviceInstance;
	}

	public void runCommand() throws Exception {
		userPrompt.intialiseCommandPrompt();
		String[] commands = userPrompt.getCommands();
		String hostOrVm = null;

		StringBuilder consolidatedCommand = new StringBuilder();
		consolidatedCommand.append(commands[0]);
		if (commands.length >= 2) {
			consolidatedCommand.append(commands[2]);
			hostOrVm = commands[1];
		}
		// Switch for commands
		switch (consolidatedCommand.toString()) {
		case "exit":
			System.exit(0);
		case "help":
			help();
		case "host":
			host(serviceInstance);
			break;
		case "vm":
			vm(serviceInstance);
			break;
		case "hostdatastore":
			hostDatastore(serviceInstance, hostOrVm);
			break;
		case "hostinfo":
			hostInfo(serviceInstance, hostOrVm);
		case "hostnetwork":
			hostNetwork(serviceInstance, hostOrVm);
		case "vminfo":
			vmInfo(serviceInstance, hostOrVm);
		case "vmon":
			vnameOn(serviceInstance, hostOrVm);
		case "vmoff":
			vnameOff(serviceInstance, hostOrVm);
		case "vmshutdown":
			vnameShutdown(serviceInstance, hostOrVm);
		default:
			System.out.println("In Valid Command");
			runCommand();
			break;
		}
	}

	private List<String> getHelp() {
		List<String> commandsList = new ArrayList<String>();
		commandsList.add("exit                         Exit the program");
		commandsList.add("help                         Prints out the uasge ");
		commandsList.add("host                         Enumerate all host");
		commandsList.add("host hname info              Show info of host hname");
		commandsList.add("host hname datastore         Enumeration datastores of host hname");
		commandsList.add("host hname network           Enumerated networks of host hname");
		commandsList.add("vm                           Enumerate all virtual machines");
		commandsList.add("vm vname info                Show info of VM vname");
		commandsList.add("vm vname on                  Power on VM vname and wait untill task completes");
		commandsList.add("vm vname off                 Power off VM vname and wait untill task completes");
		commandsList.add("vm vname shutdown            Shutdown guest of VM vname");

		return commandsList;
	}

	private void help() throws Exception {
		System.out.println("usage:");
		for (String command : getHelp()) {
			System.out.println(command);
		}
		runCommand();
	}

	/**
	 * This method will display enumeration of all hosts
	 */
	private void host(ServiceInstance serviceInstance) throws Exception {
		ManagedEntity[] host = new InventoryNavigator(serviceInstance.getRootFolder())
				.searchManagedEntities("HostSystem");
		for (int i = 0; i < host.length; i++) {
			HostSystem hostsys = (HostSystem) host[i];
			System.out.println("host[" + i + "]: Name =" + hostsys.getName());
		}
		runCommand();
	}

	/**
	 * This method will display enumeration of all data stores of host hname
	 */
	private void hostDatastore(ServiceInstance serviceInstance, String hname) throws Exception {
		HostSystem hostSystem = findHostByName(serviceInstance, hname);
		if (hostSystem != null) {
			System.out.println("Name = " + hostSystem.getName());
			int i = 0;
			for (Datastore datastore : hostSystem.getDatastores()) {
				long capacityGB = ((datastore.getSummary().capacity) / 1073741824);
				long freeSpaceGB = ((datastore.getSummary().freeSpace) / 1073741824);

				System.out.println("DataStrore[" + i + "]: name =" + datastore.getSummary().name + " Capacity = "
						+ capacityGB + " GB FreeSpace= " + freeSpaceGB + " GB");
				i++;
			}
		}
		runCommand();
	}

	/**
	 * This method will display enumeration of all info of host hname
	 */
	private void hostInfo(ServiceInstance serviceInstance, String hname) throws Exception {
		HostSystem hostSystem = findHostByName(serviceInstance, hname);
		if (hostSystem != null) {
			System.out.println("Name = " + hostSystem.getName());
			System.out.println("ProductFullName = " + hostSystem.getConfig().product.fullName);
			System.out.println("Cpu Cores = " + hostSystem.getHardware().cpuInfo.numCpuCores);
			System.out.println("RAM = " + hostSystem.getHardware().getMemorySize() / 1073741824 + " GB");
		}
		runCommand();
	}

	/**
	 * This method will display enumeration of all networkg of host hname
	 */
	private void hostNetwork(ServiceInstance serviceInstance, String hname) throws Exception {
		HostSystem hostSystem = findHostByName(serviceInstance, hname);
		if (hostSystem != null) {
			System.out.println("Name = " + hostSystem.getName());
			int i = 0;
			for (Network network : hostSystem.getNetworks()) {
				System.out.println("Network[" + i + "]: name =" + network.getName());
				i++;
			}
		}
		runCommand();
	}

	/**
	 * This method will display enumeration of all virtual machines
	 */
	private void vm(ServiceInstance serviceInstance) throws Exception {
		ManagedEntity[] machines = new InventoryNavigator(serviceInstance.getRootFolder())
				.searchManagedEntities("VirtualMachine");
		for (int i = 0; i < machines.length; i++) {
			VirtualMachine virtualMachine = (VirtualMachine) machines[i];
			System.out.println("vm[" + i + "]: Name =" + virtualMachine.getName());
		}
		runCommand();
	}

	private void vmInfo(ServiceInstance serviceInstance, String vname) throws Exception {
		VirtualMachine virtualMachine = findVirtualMachineByName(serviceInstance, vname);
		if (virtualMachine != null) {
			System.out.println("Name =" + virtualMachine.getName());
			System.out.println("Guest full name =" + virtualMachine.getGuest().guestFullName);
			System.out.println("Guest state =" + virtualMachine.getGuest().guestState);
			System.out.println("IP addr =" + virtualMachine.getSummary().getGuest().getIpAddress());
			System.out.println("Tool running status =" + virtualMachine.getGuest().toolsRunningStatus);
			System.out.println("Power state =" + virtualMachine.getSummary().runtime.getPowerState());
		}

		runCommand();

	}

	private void vnameOn(ServiceInstance serviceInstance, String vname) throws Exception {
		VirtualMachine virtualMachine = findVirtualMachineByName(serviceInstance, vname);
		if (virtualMachine != null) {
			System.out.println("Name =" + virtualMachine.getName());

			ManagedEntity[] hosts = new InventoryNavigator(serviceInstance.getRootFolder())
					.searchManagedEntities("HostSystem");
			HostSystem host = (HostSystem) hosts[0];
			Task vmPowerOnTask = virtualMachine.powerOnVM_Task(host);

			if (vmPowerOnTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Power on VM: status = " + vmPowerOnTask.waitForTask() + ", completion time = "
						+ dateFormat.format(vmPowerOnTask.getTaskInfo().getCompleteTime().getTime()));
			} else {
				System.out.println("Power on VM: status = " + vmPowerOnTask.getTaskInfo().error.localizedMessage
						+ ", completion time = "
						+ dateFormat.format(vmPowerOnTask.getTaskInfo().getCompleteTime().getTime()));
			}
		}
		runCommand();
	}

	private void vnameOff(ServiceInstance serviceInstance, String vname) throws Exception {
		VirtualMachine virtualMachine = findVirtualMachineByName(serviceInstance, vname);
		if (virtualMachine != null) {
			System.out.println("Name =" + virtualMachine.getName());

			Task vmPowerOnffTask = virtualMachine.powerOffVM_Task();

			if (vmPowerOnffTask.waitForTask() == Task.SUCCESS) {
				System.out.println("Power off VM: status = " + vmPowerOnffTask.waitForTask() + ", completion time = "
						+ dateFormat.format(vmPowerOnffTask.getTaskInfo().getCompleteTime().getTime()));
			} else {
				System.out.println("Power off VM: status = " + vmPowerOnffTask.getTaskInfo().error.localizedMessage
						+ ", completion time = "
						+ dateFormat.format(vmPowerOnffTask.getTaskInfo().getCompleteTime().getTime()));
			}
		}

		runCommand();
	}

	private void vnameShutdown(ServiceInstance serviceInstance, String vname) throws Exception {
		VirtualMachine virtualMachine = findVirtualMachineByName(serviceInstance, vname);
		if (virtualMachine != null) {
			System.out.println("Name =" + virtualMachine.getName());
			try {
				virtualMachine.shutdownGuest();
				Calendar timeLimit = Calendar.getInstance();

				// Time Limit 3 minutes
				timeLimit.add(Calendar.MINUTE, 3);
				while (Calendar.getInstance().before(timeLimit)) {
					if (virtualMachine.getSummary().getRuntime().powerState == VirtualMachinePowerState.poweredOff) {
						System.out.println("Shutdown Guest Completed," + "completion point = "
								+ dateFormat.format(Calendar.getInstance().getTime()));
						break;
					}
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				System.out.println("Graceful shutdown failed. Now Try a hard power off.");
				Task hardPowerOffTask = virtualMachine.powerOffVM_Task();
				if (hardPowerOffTask.waitForTask() == Task.SUCCESS) {
					System.out.println("Power off VM:status " + hardPowerOffTask.waitForTask() + "completion point = "
							+ dateFormat.format(hardPowerOffTask.getTaskInfo().getCompleteTime().getTime()));
				}
			}
			finally{
				Task hardPowerOffTask = virtualMachine.powerOffVM_Task();
				if (hardPowerOffTask.waitForTask() == Task.SUCCESS) {
					System.out.println("Graceful shutdown failed. Now Try a hard power off.");
					System.out.println("Power off VM:status " + hardPowerOffTask.waitForTask() + "completion point = "
							+ dateFormat.format(hardPowerOffTask.getTaskInfo().getCompleteTime().getTime()));
				}
			}
		}
		runCommand();
	}

	private static VirtualMachine findVirtualMachineByName(ServiceInstance serviceInstance, String vname)
			throws Exception {
		ManagedEntity[] machines = new InventoryNavigator(serviceInstance.getRootFolder())
				.searchManagedEntities("VirtualMachine");
		for (ManagedEntity managedEntity : machines) {
			VirtualMachine machine = (VirtualMachine) managedEntity;
			if (machine.getName().equals(vname)) {
				return machine;
			}
		}
		System.out.println("Invalid vm name = " + vname);
		return null;
	}

	private static HostSystem findHostByName(ServiceInstance serviceInstance, String hostName) throws Exception {
		ManagedEntity[] hosts = new InventoryNavigator(serviceInstance.getRootFolder())
				.searchManagedEntities("HostSystem");
		for (ManagedEntity managedEntity : hosts) {
			HostSystem hostsys = (HostSystem) managedEntity;
			if (hostsys.getName().equals(hostName)) {
				return hostsys;
			}
		}
		System.out.println("Invalid host name = " + hostName);
		return null;
	}

}
