package space.util;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import space.physics.BlackHole;
import space.physics.Star;

import java.io.InputStream;

public interface Materials {
    PhongMaterial MERCURY_MATERIAL = new PhongMaterial();
    PhongMaterial VENUS_MATERIAL = new PhongMaterial();
    PhongMaterial EARTH_MATERIAL = new PhongMaterial();
    PhongMaterial MARS_MATERIAL = new PhongMaterial();
    PhongMaterial JUPITER_MATERIAL = new PhongMaterial();
    PhongMaterial SATURN_MATERIAL = new PhongMaterial();
    PhongMaterial SATURN_RING_MATERIAL = new PhongMaterial();
    PhongMaterial URANUS_MATERIAL = new PhongMaterial();
    PhongMaterial NEPTUNE_MATERIAL = new PhongMaterial();
    PhongMaterial MOON_MATERIAL = new PhongMaterial();
    PhongMaterial[] MATERIALS = {MERCURY_MATERIAL, VENUS_MATERIAL, EARTH_MATERIAL, MARS_MATERIAL, JUPITER_MATERIAL, SATURN_MATERIAL, URANUS_MATERIAL, NEPTUNE_MATERIAL, MOON_MATERIAL};
    PhongMaterial TRAIL_MATERIAL = new PhongMaterial();

    static void load() {
        InputStream DIFFUSE_MAP = Materials.class.getResourceAsStream("/space/material/8k_earth_daymap.jpg");
        InputStream NIGHT_MAP = Materials.class.getResourceAsStream("/space/material/8k_earth_nightmap.jpg");
        InputStream NORMAL_MAP = Materials.class.getResourceAsStream("/space/material/8k_earth_normal_map.jpg");
        InputStream SPECULAR_MAP = Materials.class.getResourceAsStream("/space/material/8k_earth_specular_map.tif");
        InputStream MOON_MAP = Materials.class.getResourceAsStream("/space/material/2k_moon.jpg");
        EARTH_MATERIAL.setDiffuseMap(new Image(DIFFUSE_MAP));
        EARTH_MATERIAL.setSelfIlluminationMap(new Image(NIGHT_MAP));
        EARTH_MATERIAL.setBumpMap(new Image(NORMAL_MAP));
        EARTH_MATERIAL.setSpecularMap(new Image(SPECULAR_MAP));
        MOON_MATERIAL.setDiffuseMap(new Image(MOON_MAP));
        MERCURY_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/2k_mercury.jpg")));
        VENUS_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/2k_venus_surface.jpg")));
        MARS_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/2k_mars.jpg")));
        JUPITER_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/8k_jupiter.jpg")));
        SATURN_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/8k_saturn.jpg")));
        URANUS_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/2k_uranus.jpg")));
        NEPTUNE_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/2k_neptune.jpg")));
        TRAIL_MATERIAL.setDiffuseMap(new Image(Materials.class.getResourceAsStream("/space/material/white_pixel.png")));
        TRAIL_MATERIAL.setSelfIlluminationMap(new Image(Materials.class.getResourceAsStream("/space/material/white_pixel.png")));
        Star.load();
        BlackHole.load();
    }
}
