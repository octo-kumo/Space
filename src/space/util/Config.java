package space.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import space.Controller;
import space.physics.BlackHole;
import space.physics.Body;
import space.physics.Star;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Config {
    private static final String DEFAULT_JSON = "{\"lang\":\"en\",\"country\":\"US\",\"systems\":{\"binary_star\":[{\"vx\":0,\"vy\":0,\"vz\":1.8211692672566162,\"star\":true,\"blackHole\":false,\"mass\":99450000,\"type\":0,\"spinTime\":20,\"name\":\"Star\",\"x\":40,\"y\":0,\"z\":0,\"radius\":20,\"spinTilt\":0},{\"vx\":0,\"vy\":0,\"vz\":-1.8211692672566162,\"star\":true,\"blackHole\":false,\"mass\":99450000,\"type\":0,\"spinTime\":-20,\"name\":\"Star\",\"x\":-40,\"y\":0,\"z\":0,\"radius\":20,\"spinTilt\":0}],\"solar_system\":[{\"vx\":0,\"vy\":0,\"vz\":0,\"star\":true,\"blackHole\":false,\"mass\":19890000,\"type\":0,\"spinTime\":24,\"name\":\"Sun\",\"x\":0,\"y\":0,\"z\":0,\"radius\":7,\"spinTilt\":0},{\"vx\":3.7377328570266193,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":0.33,\"type\":0,\"spinTime\":140.76,\"name\":\"Mercury\",\"x\":0,\"y\":1.4380582521807403,\"z\":11.7120445893676,\"radius\":0.7806,\"spinTilt\":0.034},{\"vx\":2.5940530422536057,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":4.87,\"type\":1,\"spinTime\":-583.25,\"name\":\"Venus\",\"x\":0,\"y\":1.2750870318831748,\"z\":21.462156300360956,\"radius\":1.2104,\"spinTilt\":177.4},{\"vx\":2.1412030323927103,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":5.97,\"type\":2,\"spinTime\":2.39,\"name\":\"Earth\",\"x\":0,\"y\":0,\"z\":29.419999999999998,\"radius\":1.2756,\"spinTilt\":23.4},{\"vx\":1.8737084506276227,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":0.642,\"type\":3,\"spinTime\":2.46,\"name\":\"Mars\",\"x\":0,\"y\":1.3699719710139056,\"z\":41.29728292271341,\"radius\":0.6792,\"spinTilt\":25.2},{\"vx\":0.9693878622831495,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":1898,\"type\":4,\"spinTime\":0.99,\"name\":\"Jupiter\",\"x\":0,\"y\":3.359994102128919,\"z\":148.06188044069162,\"radius\":7.1492,\"spinTilt\":3.1},{\"vx\":0.7197940825445224,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":568,\"type\":5,\"spinTime\":1.07,\"name\":\"Saturn\",\"x\":0,\"y\":11.799916670070694,\"z\":270.2625249023242,\"radius\":6.0268,\"spinTilt\":26.7},{\"vx\":0.5030211362261167,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":86.8,\"type\":6,\"spinTime\":-1.72,\"name\":\"Uranus\",\"x\":0,\"y\":7.6549049927397865,\"z\":548.2065578133411,\"radius\":2.5559,\"spinTilt\":97.8},{\"vx\":0.38849521616502414,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":102,\"type\":7,\"spinTime\":1.61,\"name\":\"Neptune\",\"x\":0,\"y\":27.921023744548236,\"z\":888.4613815090988,\"radius\":2.4764,\"spinTilt\":28.3},{\"vx\":0.432095169295919,\"vy\":0,\"vz\":0,\"star\":false,\"blackHole\":false,\"mass\":0.0146,\"type\":8,\"spinTime\":-15.33,\"name\":\"Pluto\",\"x\":0,\"y\":262.39949528708524,\"z\":847.6758074128828,\"radius\":0.237,\"spinTilt\":122.5}]}}";
    private static final File CONFIG_FILE = new File("config.json");
    public static JSONObject ROOT = new JSONObject();

    public static void load() throws IOException {
        try {
            Scanner sc = new Scanner(CONFIG_FILE);
            ROOT = new JSONObject(sc.nextLine());
            if (!ROOT.has("systems")) {
                ROOT = new JSONObject(DEFAULT_JSON);
                System.out.println("Oh nooo");
            }
            sc.close();
        } catch (FileNotFoundException | NoSuchElementException e) {
            CONFIG_FILE.createNewFile();
            ROOT = new JSONObject(DEFAULT_JSON);
            save();
        }
    }

    public static void save() throws IOException {
        FileWriter fw = new FileWriter(CONFIG_FILE);
        fw.write(ROOT.toString());
        fw.close();
    }

    public static void set(String key, Object value) {
        ROOT.put(key, value);
    }

    public static int getInt(String key) {
        return ROOT.getInt(key);
    }

    public static String getString(String key) {
        return ROOT.getString(key);
    }

    public static boolean getBoolean(String key) {
        return ROOT.getBoolean(key);
    }

    public static void saveSystem(String key, ArrayList<Body> bodies) throws InvalidKeyException {
        if (key.isEmpty()) throw new InvalidKeyException(Controller.resources.getString("emptyError"));
        if (!key.matches("[a-zA-Z_]+")) throw new InvalidKeyException(Controller.resources.getString("nameError"));
        ROOT.getJSONObject("systems").put(key, toJSONArray(bodies));
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSystem(File file, ArrayList<Body> bodies) throws IOException {
        if (!file.exists()) file.createNewFile();
        FileWriter fw = new FileWriter(file);
        fw.write(toJSONArray(bodies).toString());
        fw.close();
    }

    private static JSONArray toJSONArray(ArrayList<Body> bodies) {
        JSONArray array = new JSONArray();
        bodies = (ArrayList<Body>) bodies.clone();
        for (Body body : bodies) {
            if (!body.alive) continue;
            JSONObject obj = new JSONObject();
            obj.put("name", body.name);
            obj.put("x", body.getPosition().x);
            obj.put("y", body.getPosition().y);
            obj.put("z", body.getPosition().z);
            obj.put("vx", body.getVelocity().x);
            obj.put("vy", body.getVelocity().y);
            obj.put("vz", body.getVelocity().z);
            obj.put("mass", body.getMass());
            obj.put("radius", body.getRadius());
            obj.put("type", body.type);
            obj.put("spinTime", body.spinPeriod);
            obj.put("spinTilt", body.tilt.getAngle());
            obj.put("star", body instanceof Star);
            obj.put("blackHole", body instanceof BlackHole);
            array.put(obj);
        }
        return array;
    }

    public static ArrayList<Body> loadSystem(String key) throws JSONException, InvalidKeyException {
        if (key.isEmpty()) throw new InvalidKeyException(Controller.resources.getString("emptyError"));
        if (!key.matches("[a-zA-Z_]+")) throw new InvalidKeyException(Controller.resources.getString("nameError"));
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray array = ROOT.getJSONObject("systems").getJSONArray(key);
        return getBodies(array);
    }

    public static ArrayList<Body> loadSystem(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        String json = "";
        while (sc.hasNextLine()) json = json + sc.nextLine();
        sc.close();
        JSONArray array = new JSONArray(json);
        return getBodies(array);
    }

    private static ArrayList<Body> getBodies(JSONArray array) {
        ArrayList<Body> bodies = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Body finalBody;
            if (obj.getBoolean("blackHole"))
                finalBody = new BlackHole(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z"), obj.getDouble("vx"), obj.getDouble("vy"), obj.getDouble("vz"), obj.getDouble("mass"), obj.getDouble("radius"));
            else if (obj.getBoolean("star"))
                finalBody = new Star(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z"), obj.getDouble("vx"), obj.getDouble("vy"), obj.getDouble("vz"), obj.getDouble("mass"), obj.getDouble("radius"), obj.getDouble("spinTime"));
            else
                finalBody = new Body(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z"), obj.getDouble("vx"), obj.getDouble("vy"), obj.getDouble("vz"), obj.getDouble("mass"), obj.getDouble("radius"), obj.getInt("type"), obj.getDouble("spinTime"));
            finalBody.name = obj.getString("name");
            finalBody.tilt.setAngle(obj.getDouble("spinTilt"));
            bodies.add(finalBody);
        }
        return bodies;
    }
}
