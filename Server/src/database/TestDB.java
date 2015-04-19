//import database.DbInteract;

public class TestDB {
	public static void main(String[] args) {
		DbInteract db = null;
		String u1 = "user1";
		String u2 = "user2";

		String g1 = "g1,f1,f2,f3,f4,f5";
		String g2 = "g2,f6,f7,f8,f9,f10";
		String g3 = "g3,f11,f12,f13";

		db = new DbInteract();

		addUsers(db,u1,u2);
		assignGroups(db,u1,u2,g1,g2,g3);
		viewContents(db, "Groups added", u1,u2);
		
		db.removeGroup(u1, "g1");

		viewContents(db,"Group 1 removed", u1,u2);
		clearGroups(db, u1, u2);
		viewContents(db, "All groups removed", u1,u2);
		db.closeConnection();
		db = null;
	}

	public static void addUsers(DbInteract db, String u1, String u2) {
		db.addUser(u1);
		db.addUser(u2);
	}

	public static void assignGroups(DbInteract db, String u1, String u2, String g1, String g2, String g3) {
		db.addGroup(u1, g1);
		db.addGroup(u1, g2);
		db.addGroup(u2, g3);
	}

	public static void viewContents(DbInteract db, String message, String u1, String u2) {
		System.out.println("``````````" + message + "``````````");
		String[] groups1 = db.getGroupsByID(u1);
		String[] groups2 = db.getGroupsByID(u2);
		
		for(String a : groups1) {
			System.out.println(a);
		}
		for(String b : groups2) {
			System.out.println(b);
		}
	}
	public static void clearGroups(DbInteract db, String u1, String u2) {
		db.removeGroup(u1, "*");
		db.removeGroup(u2, "*");
	}
}
