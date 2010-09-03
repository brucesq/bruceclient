package cn.hunthawk.j2me.bo;

import de.enough.polish.io.Serializable;

public class InitControler implements Serializable
{
private String Name;
private int SoftVersion;
private int PropVersion;
private int homeVersion;
private String propURL;
private String homeURL;
private String softURL;

private String invitation;



/**
 * @return the name
 */
public String getName()
{
	return Name;
}

/**
 * @param name the name to set
 */
public void setName(String name)
{
	Name = name;
}

/**
 * @return the softVersion
 */
public int getSoftVersion()
{
	return SoftVersion;
}

/**
 * @param softVersion the softVersion to set
 */
public void setSoftVersion(int softVersion)
{
	SoftVersion = softVersion;
}

/**
 * @return the propVersion
 */
public int getPropVersion()
{
	return PropVersion;
}

/**
 * @param propVersion the propVersion to set
 */
public void setPropVersion(int propVersion)
{
	PropVersion = propVersion;
}

/**
 * @return the propURL
 */
public String getPropURL()
{
	return propURL;
}

/**
 * @param propURL the propURL to set
 */
public void setPropURL(String propURL)
{
	this.propURL = propURL;
}


/**
 * @return the homeVersion
 */
public int getHomeVersion()
{
	return homeVersion;
}

/**
 * @param homeVersion the homeVersion to set
 */
public void setHomeVersion(int homeVersion)
{
	this.homeVersion = homeVersion;
}

/**
 * @return the homeURL
 */
public String getHomeURL()
{
	return homeURL;
}

/**
 * @param homeURL the homeURL to set
 */
public void setHomeURL(String homeURL)
{
	this.homeURL = homeURL;
}

/**
 * @return the softURL
 */
public String getSoftURL()
{
	return softURL;
}

/**
 * @param softURL the softURL to set
 */
public void setSoftURL(String softURL)
{
	this.softURL = softURL;
}



/**
 * @return the invitation
 */
public String getInvitation()
{
	return invitation;
}

/**
 * @param invitation the invitation to set
 */
public void setInvitation(String invitation)
{
	this.invitation = invitation;
}
}
