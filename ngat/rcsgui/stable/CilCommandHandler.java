package ngat.rcsgui.stable;

import java.rmi.*;
import java.rmi.server.*;
import java.awt.*;
import javax.swing.*;
import ngat.net.cil.*;

/** Clients which wish to be informed of responses to CIL messages should implement
 * CilResponseHandler.
 */
public class CilCommandHandler extends UnicastRemoteObject implements CilResponseHandler {

    Component root;

    public CilCommandHandler(Component root) throws  RemoteException {
	super();
	this.root = root;
    }

    /** Handle a <i>completion</i> reply. 
     *@param message The reply message.
    */
    @Override
	public void completed(String message) throws RemoteException {
	display("Command response", message);
    }

    /** Handle an <i>actioned</i> reply.*/
    @Override
	public void actioned() throws RemoteException {}

    /** Handle an <i>error</i> reply.
     * @param code The error code.
     *@param message The error message.
     */
    @Override
	public void error(int code, String message) throws RemoteException {
	display("Command Failed", "Code: "+code+" : "+message);
    }

    /**
     * Handle a CIL response timeout
     * @param message
     * @throws RemoteException
     */
    @Override
	public void timedout(String message) throws RemoteException {
	display("Command Timedout", message);
    }
    
    private void display(String header, String message) {
	JOptionPane.showMessageDialog(root, message, header, JOptionPane.INFORMATION_MESSAGE);
    }
}
