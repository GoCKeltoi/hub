package de.mobile.inventorylistindexer.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

class ReleaseInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (InputStream in = Resources.getResource("release-info").openStream()) {
            ByteStreams.copy(in, resp.getOutputStream());
            resp.setStatus(200); // set status to 200 only if everything above succeeds
        }
    }

}