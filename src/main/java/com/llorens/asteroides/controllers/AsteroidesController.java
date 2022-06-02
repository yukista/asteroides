package com.llorens.asteroides.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.llorens.asteroides.clases.Asteroide;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;


import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/asteroides")
public class AsteroidesController {

    @GetMapping("/asteroids")
    public List<Asteroide> hola(int days) throws Exception {
        if(days == 0) {
            throw new Exception("days parameter is mandatory and has to be between the numbers 1-7");
        } else if(days < 1 || days > 7) {
            throw new Exception("days parameter has to be between the numbers 1-7");
        } else {
            //Listado de asteroides que devolveremos
            List<Asteroide> asteroides = new ArrayList<>();

            //Recuperamos la fecha de hoy y luego incrementamos en X dias para obtener la fecha final
            Date fechaActual = new Date();
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(fechaActual);
            calendario.add(Calendar.DATE, days);
            Date fechaFinal = calendario.getTime();
            //Obtenemos los strings con las fechas formateadas a dia - mes - año
            SimpleDateFormat formatoAnoMesDia = new SimpleDateFormat("yyyy-MM-dd");
            String stringFechaActual = formatoAnoMesDia.format(fechaActual);
            String stringFechaFinal = formatoAnoMesDia.format(fechaFinal);
            //Dirección del servicio web de la NASA añadiendo las fechas formateadas
            String uri = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + stringFechaActual + "&end_date=" + stringFechaFinal + "&api_key=DEMO_KEY";

            RestTemplate restTemplate = new RestTemplate();
            String datosNasa = restTemplate.getForObject(uri, String.class);
            JSONObject objectoRaiz = new JSONObject(datosNasa);
            JSONObject objectoObjectosCercaDeLaTierra = objectoRaiz.getJSONObject("near_earth_objects");
            JSONArray arrayNombresObjetos = objectoObjectosCercaDeLaTierra.names();
            for(int i = 0; i < arrayNombresObjetos.length(); i++)
            {
                String stringObjetoDia = arrayNombresObjetos.getString(i);
                JSONArray arrayDia = objectoObjectosCercaDeLaTierra.getJSONArray(stringObjetoDia);
                for(int j = 0; j < arrayDia.length(); j++) {
                    JSONObject asteroide = arrayDia.getJSONObject(j);
                    //Recuperamos el nombre del asteroide del parametro "name"
                    String nombre = asteroide.getString("name");
                    //Recuperamos el boolean que nos indica si el asteroide es potencialmente peligroso
                    Boolean peligroso = asteroide.getBoolean("is_potentially_hazardous_asteroid");
                    //Recuperamos los diametros máximos y mínimos del asteroide para hacer una media y obtener su diametro aproximado
                    JSONObject estimatedDiameter = asteroide.getJSONObject("estimated_diameter");
                    JSONObject kilometers = estimatedDiameter.getJSONObject("kilometers");
                    Double estimatedDiameterMin = kilometers.getDouble("estimated_diameter_min");
                    Double estimatedDiameterMax = kilometers.getDouble("estimated_diameter_max");
                    Double promedioDiametro = (estimatedDiameterMax + estimatedDiameterMin) / 2;
                    //Recuperamos los datos de aproximación para obtener la fecha de aproximación, la velocidad y el planeta al que se aproxima
                    JSONArray closeApproachDataArray = asteroide.getJSONArray("close_approach_data");
                    if(closeApproachDataArray.length() > 0) {
                        //TODO: Estamos recuperando el primer elemento de un array. Posiblemente sea un error y pueda haber más elementos, probablemente se pueda aproximar a más de un planeta.
                        JSONObject closeApproachData = closeApproachDataArray.getJSONObject(0);
                        JSONObject relativeVelocity = closeApproachData.getJSONObject("relative_velocity");
                        Double kilometersPerHour = relativeVelocity.getDouble("kilometers_per_hour");
                        String fecha = closeApproachData.getString("close_approach_date");
                        String planeta = closeApproachData.getString("orbiting_body");
                        //Si el asteroide és potencialmente peligroso y el planeta al que se aproxima és la tierra lo añadimos a nuestro listado
                        if (peligroso && planeta.equals("Earth")) {
                            Asteroide ast = new Asteroide();
                            ast.setNombre(nombre);
                            ast.setDiametro(promedioDiametro);
                            ast.setVelocidad(kilometersPerHour);
                            ast.setFecha(fecha);
                            ast.setPlaneta(planeta);
                            asteroides.add(ast);
                        }
                    }
                }
            }
            return asteroides;
        }
    }

}
