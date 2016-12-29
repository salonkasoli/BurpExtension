package burp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;


public class BurpExtender implements IBurpExtender, IContextMenuFactory{

	private IBurpExtenderCallbacks callbacks;
	
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		
		this.callbacks = callbacks;
		
		
		callbacks.setExtensionName("REQUESTSENDER");
		callbacks.registerContextMenuFactory(BurpExtender.this);
	}

	
	//IContextMenu Implementation
	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		
		List<JMenuItem> menuitemlist = new ArrayList<JMenuItem>();
		IHttpRequestResponse[] messages = invocation.getSelectedMessages();
		if (messages.length > 0){
			menuitemlist.add(new JMenuItem("TEST"));
		}
		
		
		return menuitemlist;
	}

	
	
	

}
