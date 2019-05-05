package com.hanista.mobogram.messenger.volley.toolbox;

import com.hanista.mobogram.messenger.volley.Request;
import java.util.Map;
import org.apache.http.HttpResponse;

public interface HttpStack {
    HttpResponse performRequest(Request<?> request, Map<String, String> map);
}
