package org.jeromegout.simplycloud.hosts;

import android.util.Log;

import org.jeromegout.simplycloud.hosts.free.FreeHost;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HostManager {

	public static final HostManager instance = new HostManager();
	private Map<String, HostServices> hosts;
	private String currentId = FreeHost.HOST_ID;

	private HostManager() {
		hosts = new HashMap<>();
	}

	public void registerHost(HostServices host) {
		if(!hosts.keySet().contains(host.getHostId())) {
			hosts.put(host.getHostId(), host);
		} else {
			Log.e("ERROR == ", host.getHostId()+" has been already registered");
		}
	}

	public void setCurrentId(String id) {
		if(hosts.keySet().contains(id))  {
			currentId = id;
		}
	}

	public Set<String> getHostIds() {
		return hosts.keySet();
	}

	public HostServices getHostById(String id) {
		return hosts.get(id);
	}

	public HostServices getCurrentHost() {
		return getHostById(currentId);
	}

	/**
	 * Collects all hosts
	 */
	public void init() {
		registerHost(new FreeHost());
	}
}
