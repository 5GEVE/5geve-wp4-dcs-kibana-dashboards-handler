package com.telcaria.dcs.nbi.wrapper;

import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class DashboardWrapper {

  @NonNull
  public List<UrlWrapper> urls;

}
