/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ngat.net.cil.CilResponseHandler;
import ngat.rcsgui.stable.CilRow;
import ngat.rcsgui.stable.CilTableModel;

/**
 * @author eng
 *
 */
public class GuiCilResponseHandler extends UnicastRemoteObject implements CilResponseHandler {

	private CilTableModel ctm;
	private CilRow crow;
	private long sendTime;
	private int irow;
	
	/**
	 * @throws RemoteException
	 */
	public GuiCilResponseHandler(CilTableModel ctm, CilRow crow, int irow) throws RemoteException {
		super();
		this.ctm = ctm;
		this.crow = crow;
		this.irow = irow;
		sendTime = System.currentTimeMillis();
		System.err.println("Create row: "+irow+" w "+crow.message);
	}

	

	/* (non-Javadoc)
	 * @see ngat.net.cil.CilResponseHandler#actioned()
	 */
	@Override
	public void actioned() throws RemoteException {
		System.err.println("Actioned");
		crow.status = CilRow.ACTIONED;
		crow.elapsedTime = (System.currentTimeMillis() - sendTime);	
		ctm.fireTableRowsUpdated(irow, irow);
	}

	/* (non-Javadoc)
	 * @see ngat.net.cil.CilResponseHandler#completed(java.lang.String)
	 */
	@Override
	public void completed(String message) throws RemoteException {
		// update a row
		System.err.println("Completed: "+message);
		crow.status = CilRow.COMPLETED;
		crow.elapsedTime = (System.currentTimeMillis() - sendTime);	
		ctm.fireTableRowsUpdated(irow, irow);
	}

	/* (non-Javadoc)
	 * @see ngat.net.cil.CilResponseHandler#error(int, java.lang.String)
	 */
	@Override
	public void error(int errCode, String errMessage) throws RemoteException {
		System.err.println("Error: "+errCode+" : "+errMessage);
		crow.status = CilRow.FAILED;
		crow.elapsedTime = (System.currentTimeMillis() - sendTime);	
		ctm.fireTableRowsUpdated(irow, irow);
	}

	/* (non-Javadoc)
	 * @see ngat.net.cil.CilResponseHandler#timedout(java.lang.String)
	 */
	@Override
	public void timedout(String message) throws RemoteException {
		System.err.println("Timed out: "+message);
		crow.status = CilRow.TIMED_OUT;
		crow.elapsedTime = (System.currentTimeMillis() - sendTime);	
		ctm.fireTableRowsUpdated(irow, irow);
	}

}
