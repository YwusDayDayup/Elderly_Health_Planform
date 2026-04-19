package com.example.service.external;

import com.example.common.constant.CareConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class MockMapGateway implements MapGateway {

  private final ExternalCallLogService externalCallLogService;

  public MockMapGateway(ExternalCallLogService externalCallLogService) {
    this.externalCallLogService = externalCallLogService;
  }

  @Override
  public String resolveAddress(BigDecimal latitude, BigDecimal longitude, String fallbackAddress) {
    String address = fallbackAddress;
    if (address == null || address.isBlank()) {
      address = "模拟定位点(" + latitude.setScale(3, RoundingMode.HALF_UP)
          + "," + longitude.setScale(3, RoundingMode.HALF_UP) + ")";
    }
    externalCallLogService.log(CareConstants.CALL_MAP, "REVERSE_GEOCODE", "LOCATION", null,
        "{\"latitude\":" + latitude + ",\"longitude\":" + longitude + "}",
        "{\"address\":\"" + address + "\"}", true);
    return address;
  }

  @Override
  public boolean withinZone(BigDecimal latitude, BigDecimal longitude, BigDecimal centerLatitude,
      BigDecimal centerLongitude, Integer radiusMeters) {
    double dx = latitude.subtract(centerLatitude).doubleValue() * 111_000D;
    double dy = longitude.subtract(centerLongitude).doubleValue() * 111_000D;
    double distance = Math.sqrt(dx * dx + dy * dy);
    boolean within = distance <= (radiusMeters == null ? 500D : radiusMeters.doubleValue());
    externalCallLogService.log(CareConstants.CALL_MAP, "GEOFENCE_CHECK", "LOCATION", null,
        "{\"latitude\":" + latitude + ",\"longitude\":" + longitude + "}",
        "{\"within\":" + within + ",\"distance\":" + String.format("%.2f", distance) + "}", true);
    return within;
  }
}
