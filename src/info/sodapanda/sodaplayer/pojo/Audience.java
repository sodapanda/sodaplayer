package info.sodapanda.sodaplayer.pojo;

public class Audience {
	int uid;//用户ID
	String nk;//用户昵称
	int rk;//用户等级
	int pk;//用户类型 游客 0，普通用户 1，房管 2，主播 3，管理员 4
	int vip;//vip类型 1黄  2紫色
	String avatar;//头像
	boolean is_actor;
	boolean is_manager;
	
	
	@Override
	public String toString() {
		return nk;
	}
	public Audience(int uid, String nk, int rk, int pk, int vip,String avatar) {
		super();
		this.uid = uid;
		this.nk = nk;
		this.rk = rk;
		this.pk = pk;
		this.vip = vip;
		this.avatar = avatar;
	}
	public int getUid() {
		return uid;
	}
	public String getNk() {
		return nk;
	}
	public int getRk() {
		return rk;
	}
	public int getPk() {
		return pk;
	}
	public int getVip() {
		return vip;
	}
	public String getAvatar(){
		return avatar;
	}
	
}
