package burp;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;


public class BurpExtender implements IBurpExtender, IContextMenuFactory{

	protected static final String HOST = "127.0.0.1";
	protected static final String PORT = "8080";
	private IBurpExtenderCallbacks callbacks;
	private PrintWriter stdout;
	private IExtensionHelpers helpers;
	

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        
		callbacks.setExtensionName("REQUEST SENDER");
		callbacks.registerContextMenuFactory(BurpExtender.this);		
	}
	
	//IContextMenu Implementation
	@Override
	public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {
		
		List<JMenuItem> menuItemList = new ArrayList<JMenuItem>();
		if ((invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST)
				|| (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST)){
			JMenuItem item = new JMenuItem(new AbstractAction("SEND IT"){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					IHttpRequestResponse[] requestArray = invocation.getSelectedMessages();
					sendToAnotherHost(requestArray[0],HOST,PORT);
				}
				
			});
			menuItemList.add(item);
		}
		
		
		return menuItemList;
	}
	
	public void sendToAnotherHost(IHttpRequestResponse request, String host, String port){
		stdout.println(host);
		stdout.println(port);
	}
}