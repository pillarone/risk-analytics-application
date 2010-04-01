package models.application;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class TestClaim extends SingleValuePacket {
    private double ultimate;
    private TestClaim originalClaim;
    private Double fractionOfPeriod = 0d;

    public TestClaim() {
    }
}
