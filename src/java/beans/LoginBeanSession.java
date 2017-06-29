package beans;

import javax.servlet.http.*;
import Arquivo.Arquivo;
import User.Client;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.concurrent.Cancellable;
import javax.faces.component.UIInput;
/**
 *
 * @author prog
 */

public class LoginBeanSession {
}