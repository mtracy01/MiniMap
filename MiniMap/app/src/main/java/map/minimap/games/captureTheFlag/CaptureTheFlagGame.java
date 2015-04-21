package map.minimap.games.captureTheFlag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import map.minimap.MainMenu;
import map.minimap.frameworks.gameResources.Beacon;
import map.minimap.frameworks.gameResources.Flag;
import map.minimap.frameworks.gameResources.Game;
import map.minimap.frameworks.gameResources.Team;
import map.minimap.frameworks.mapResources.Maps;
import map.minimap.frameworks.gameResources.User;
import map.minimap.helperClasses.Data;


/**
 * Created by michael on 4/19/2015.
 */
public class CaptureTheFlagGame extends Game {

    Flag redFlag;
    Flag blueFlag;
    LatLng startLoc; // endpoints of the line of scrimmage
    LatLng endLoc;
    Polyline lineOfScrimmage = null;
    User team2Carrier = null;
    User team3Carrier = null;

    public CaptureTheFlagGame() {
        beaconsEnabled = true;
        teams = new ArrayList<Team>();
        teams.add(new Team(2));
        teams.add(new Team(3));
    }

    @Override
    public void handleMessage(String message) {
        // TODO Auto-generated method stub
        Log.v("CTF Game", message); //I just changed this to Sardines...
        final String[] parts = message.split(" ");
        if (parts[0].equals("location")) {
            User user = findUserbyId(parts[1], Data.players);
            if (user == null) {
                return;
            }
            LatLng ll = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            user.setTeam(Integer.parseInt(parts[4]));

            Log.v("userid", parts[1]);
            if (user == null) {
                Log.v("userid", "is null");
            }
            user.setCoordinates(ll);

            // We can only update locations from the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    if (Data.map == null) {
                        return;
                    }
                    ArrayList<User> teammates = new ArrayList<>();
                    for (User u : Data.players) {
                        if (u.getMarker() != null) {
                            u.getMarker().remove();
                        }
                        if (u.getTeam() == Data.user.getTeam()) {
                            teammates.add(u);
                        }
                    }
                    // Show the team carriers
                    if (Data.user.getTeam() == 2) {
                        if (team3Carrier != null) {
                            teammates.add(team3Carrier);
                        }
                    } else {
                        if (team2Carrier != null) {
                            teammates.add(team2Carrier);
                        }
                    }
                    Data.map.clear();
                    Maps.initializePlayers(Data.map, teammates);
                    Log.v("CTF", "Drawing line of scrimmage");
                    lineOfScrimmage = Data.map.addPolyline(new PolylineOptions()
                            .add(startLoc, endLoc)
                            .width(5)
                            .color(Color.RED));
                    lineOfScrimmage.setVisible(true);

                    // Only show the flags if they are not being carried
                    if (blueFlag != null && team3Carrier == null) {
                        Log.v("CTF", "Drawing blue flag");
                        blueFlag.show();
                    } else {
                        if (blueFlag != null) {
                            blueFlag.hide();
                        }
                    }
                    if (redFlag != null && team2Carrier == null) {
                        Log.v("CTF", "Drawing red flag");
                        redFlag.show();
                    } else {
                        if (redFlag != null) {
                            redFlag.hide();
                        }
                    }
                }
            });

        } else if (parts[0].equals("addbeacon")) {

            LatLng b = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            Beacon beac = new Beacon(b, Integer.parseInt(parts[1]), Data.user.getTeam());
            Data.user.addBeacon(beac);

        } else if (parts[0].equals("removebeacon")) {
            Data.user.removeBeaconByID((Integer.parseInt(parts[1])));

        } else if (parts[0].equals("acceptTagged")) {
            //make a dialogue to ask if ___ has found this user
            final String id = parts[1];
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    final User targetUser = findUserbyId(id, Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Has " + targetUser.getName() + " tagged you?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTagged true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTagged false");
                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else if (parts[0].equals("acceptTag")) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                public void run() {
                    Log.v("CTF Game", "Running on ui thread");
                    User targetUser = findUserbyId(parts[1], Data.players);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Data.gameActivity);
                    // Add the buttons
                    builder.setMessage("Confirm tag of " + targetUser.getName() + "?");
                    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTag true");
                        }
                    });
                    builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Data.client.sendMessage("confirmTag false");
                        }
                    });
                    Log.v("CTF Game", "builder created");
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.v("CTF Game", "dialog show");
                }
            });
        } else if (parts[0].equals("tag")) {
            final String assassinID = parts[1];
            String targetID = parts[2];
            if (targetID.equals(Data.user.getID())) {
                Data.mainAct.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.mainAct.getApplicationContext(), "You were tagged by " + findUserbyId(assassinID, Data.players).getName() + ".", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        } else if (parts[0].equals("flag")) {
            LatLng loc = new LatLng(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            if (Integer.parseInt(parts[1]) == 2) {
                blueFlag = new Flag(loc, teams.get(0));
            } else {
                redFlag = new Flag(loc, teams.get(1));
            }
        } else if (parts[0].equals("lineOfScrimmage")) {
            startLoc = new LatLng(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            endLoc = new LatLng(Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
        } else if (parts[0].equals("userRemoved")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.user.setInGame(false);
                Data.user.setGame(null);
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You have been removed from the game.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Data.gameActivity.startActivity(new Intent(Data.gameActivity, MainMenu.class));
            } else {
                final User removedUser = findUserbyId(parts[1], Data.players);
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), removedUser.getName() + " has left the game.", Toast.LENGTH_SHORT);
                        toast.show();
                        removedUser.getMarker().remove();
                        Data.players.remove(removedUser);
                    }
                });
            }
        } else if (parts[0].equals("flagPickup")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You picked up the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            } else {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        String name = findUserbyId(parts[1], Data.players).getName();
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(),  name + " has picked up the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            User flagCarrier = findUserbyId(parts[1], Data.players);
            int otherTeam = (flagCarrier.getTeam() == 2) ? 3 : 2;
            if (otherTeam == 2) {
                blueFlag.hide();
                team3Carrier = flagCarrier;
            }
            else {
                redFlag.hide();
                team2Carrier = flagCarrier;
            }
        } else if (parts[0].equals("flagReturned")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You returned the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            } else {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        String name = findUserbyId(parts[1], Data.players).getName();
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(),  name + " has returned the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            User flagCarrier = findUserbyId(parts[1], Data.players);
            int otherTeam = (flagCarrier.getTeam() == 2) ? 3 : 2;
            if (otherTeam == 2) {
                blueFlag.show();
                team3Carrier = null;
            }
            else {
                redFlag.show();
                team2Carrier = null;
            }
        } else if (parts[0].equals("flagCaptured")) {
            if (parts[1].equals(Data.user.getID())) {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(), "You captured the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Data.client.sendMessage("remove " + Data.gameId + " " + Data.user.getID());
                Data.gameActivity.startActivity(new Intent(Data.gameActivity,MainMenu.class));
            } else {
                Data.gameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        String name = findUserbyId(parts[1], Data.players).getName();
                        Toast toast = Toast.makeText(Data.gameActivity.getApplicationContext(),  name + " has captured the flag.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Data.client.sendMessage("remove " + Data.gameId + " " + Data.user.getID());
                Data.gameActivity.startActivity(new Intent(Data.gameActivity,MainMenu.class));
            }
        } else if (parts[0].equals("team")) {
            User u = Data.user.findUserById(parts[2]);
            Log.v("CTF", "Setting team for user: " + u);
            if (u != null) {
                u.setTeam(Integer.parseInt(parts[1]));
                if (parts[1].equals("2")) {
                    teams.get(0).addUser(u);
                } else {
                    teams.get(1).addUser(u);
                }
            }
        }

    }

    public User findUserbyId(String theid, ArrayList<User> users) {
        for (User u: users) {
            if (u.getID().equals(theid)) {
                return u;
            }
        }
        return null;

    }


    @Override
	/* called when user presses start button
	 * assign teams, etc...
	 *  */
    public void startSession() {
        Log.v("Capture The Flag Game", "Starting game session " + this.getId());
        isRunning = true;
        redFlag.show();
        blueFlag.show();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            public void run() {
                if (Data.map != null) {
                    lineOfScrimmage = Data.map.addPolyline(new PolylineOptions()
                            .add(startLoc, endLoc)
                            .width(5)
                            .color(Color.RED));
                    lineOfScrimmage.setVisible(true);
                }
            }
        });

    }

    @Override
	/*
	 * everyone is gone?
	 * @see sessions.GameSession#endSession()
	 */
    public void endSession() {
        isRunning = false;
    }

    @Override
    public void removeUser(User user) {
        Log.v("Capture the Flag Game", "Removing user from ctf session");
    }

    @Override
	/* mid-game */
    public void addUser(User user, int teamid) {
        // TODO Auto-generated method stub
    }

    /**
     * teamID should always be 0 in FriendFinder
     */

    @Override
    public void addBeacon(int teamid, LatLng loc) {
        // TODO Auto-generated method stub
        // Beacon beacon = new Beacon(loc);
        // beacon.setTeamId(teamid);
        // getTeambyID(teams, teamid).addBeacon(beacon);

    }

    /**
     * teamID should always be 0
     */
    @Override
    public void removeBeacon(Integer id) {
        // TODO Auto-generated method stub
    }



}
