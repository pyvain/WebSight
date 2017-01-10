package fr.pyvain.websight.websight.PersonalDataGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 *     @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */

public class TestPolarCoords {

    private static final float EPS = 0.01f;

    @BeforeClass
    public static void beforeTests() {
        System.out.println("Testing class PolarCoords\n");
    }

    @Test
    public void testPolarCoords() {
        Random rand = new Random();
        int r = rand.nextInt();
        float angle = rand.nextFloat();
        PolarCoords coords = new PolarCoords(r, angle);
        assertEquals(r, coords.getRadius());
        assertEquals(angle, coords.getAngle(), EPS);
    }

    @Test
    public void testEqualHashCode() {
        Random rand = new Random();
        int r = rand.nextInt();
        float angle = rand.nextFloat();
        PolarCoords coords0 = new PolarCoords(r, angle);
        PolarCoords coords0bis = new PolarCoords(r, angle);
        r = rand.nextInt();
        angle = rand.nextFloat();
        PolarCoords coords1 = new PolarCoords(r, angle);

        assertEquals(coords0, coords0bis);
        assertEquals(coords0.hashCode(), coords0bis.hashCode());
        assertEquals(coords0bis, coords0);
        assertEquals(coords0bis.hashCode(), coords0.hashCode());
        assertNotEquals(coords0, coords1);
        assertNotEquals(coords1, coords0);
    }

}