package agents;

import gui.ChatFrame;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import behaviors.BhvSwitchInfoLink;
import behaviors.user.ChatReceptionBehaviour;
import behaviors.user.SendMessageBehaviour;

import com.google.gson.GsonBuilder;

public class UserAgent extends BaseAgent {


	private GsonBuilder gsonb;

	private List<String> LinkTable;
	
	public PropertyChangeSupport changes;
	private ChatFrame frame;
	public static String MSG_RECEIVED_PROP = "MSG_RECEIVED_PROP";
	public static int SEND_MESSAGE_EVENT = 1;
	public static int RECEIVED_MESSAGE_EVENT = 2;

	public String interlocuteur;
	
	public UserAgent() {
		changes = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);		
	}
	
	protected void setup() {
		log("initialisation");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("UserAgent");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		Object[] args = getArguments();
		interlocuteur = (String) args[0];
		
		frame = new ChatFrame(this);
		

		addBehaviour(new BhvSwitchInfoLink(this));
		addBehaviour(new ChatReceptionBehaviour(this, 0));
		
		/*
		if (args[0] != null) {
			interlocuteur = (String) args[0];	
			log("envoie des entiers à "+args[0]);
		}			
		addBehaviour(new BhvUserIncCom(this, 2, (String) args[0]));
		*/
	}

	public AID searchMasterAgent() {

		AID masterAgent = null;

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MasterAgent");
		template.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, template);

			if (result.length >= 1)
				masterAgent = result[0].getName();

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		return masterAgent;
	}
	
	
	protected void onGuiEvent(GuiEvent ev) {
		
		if (ev.getType() == SEND_MESSAGE_EVENT) {
			String s = (String) ev.getParameter(0);
								
			this.addBehaviour(new SendMessageBehaviour(this.getLocalName(), (String) ev.getParameter(1), s));
		} else if (ev.getType() == RECEIVED_MESSAGE_EVENT) {
				frame.addMessage((String) ev.getParameter(0));
		}
		
	}

}
