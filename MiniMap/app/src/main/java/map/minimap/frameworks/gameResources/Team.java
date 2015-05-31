package map.minimap.frameworks.gameResources;

import java.util.ArrayList;

public class Team {

    /**
     * Store the players in the team
     */
    private ArrayList<User> users;
    private int teamID;

    public Team(int tid) {
        users = new ArrayList<>();
        this.teamID = tid;
    }

    /**
     * Add a user to the team
     *
     * @param u
     */
    public void addUser(User u) {
        synchronized (users) {
            users.add(u);
        }
    }

    /**
     * Remove a user from the team
     *
     * @param u
     */
    public void removeUser(User u) {
        synchronized (users) {
            users.remove(u);
        }
    }

    public boolean containsUser(User user) {
        for (User u : users) {
            if (u.getID().equals(user.getID())) {
                return true;
            }
        }
        return false;
    }

    public int getTeamID() {
        return teamID;
    }
}
