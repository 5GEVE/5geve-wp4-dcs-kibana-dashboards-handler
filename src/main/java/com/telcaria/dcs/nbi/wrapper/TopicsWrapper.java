package com.telcaria.dcs.nbi.wrapper;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class TopicsWrapper {

  @NonNull
  public List<TopicWrapper> topics;

}
