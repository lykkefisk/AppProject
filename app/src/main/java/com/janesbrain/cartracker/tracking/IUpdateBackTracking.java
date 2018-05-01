package com.janesbrain.cartracker.tracking;

import java.util.List;

public interface IUpdateBackTracking {
    void OnStartTracking();
    void OnSuccess(List<Route> route);
}
