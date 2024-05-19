package aplicacionesmoviles.avanzado.todosalau.ejemploopenstreetmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Código de solicitud para permisos
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    // Instancias de elementos de la interfaz de usuario y geocoder
    private MapView mapView;
    private Geocoder geocoder;
    private EditText editTextLocation;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar la biblioteca de OpenStreetMap
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        // Inicializar el geocoder
        geocoder = new Geocoder(this);

        // Referenciar los elementos de la interfaz de usuario
        mapView = findViewById(R.id.map);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        Button buttonSearchCity = findViewById(R.id.buttonSearchCity);
        Button buttonSearchLatLong = findViewById(R.id.buttonSearchLatLong);

        // Configurar el evento click del botón de búsqueda por ciudad
        buttonSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocationByName();
            }
        });

        // Configurar el evento click del botón de búsqueda por latitud y longitud
        buttonSearchLatLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocationByLatLong();
            }
        });

        // Verificar y solicitar permisos de ubicación
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            setupMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // Comprobar si los permisos de ubicación están otorgados
    private boolean checkPermissions() {
        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    // Solicitar permisos de ubicación
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    // Manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap();
            } else {
                Toast.makeText(this, "La aplicación necesita permisos de ubicación para funcionar correctamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Configurar el mapa
    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
    }

    // Buscar ubicación por nombre
    private void searchLocationByName() {
        String locationName = editTextLocation.getText().toString().trim();
        if (!locationName.isEmpty()) {
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                if (!addresses.isEmpty()) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    mapView.getController().setCenter(new GeoPoint(latitude, longitude));
                    mapView.getController().setZoom(15.0);
                } else {
                    Toast.makeText(this, "No se pudo encontrar la ubicación especificada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al buscar la ubicación", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese un nombre de ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    // Buscar ubicación por latitud y longitud
    private void searchLocationByLatLong() {
        String latitudeStr = editTextLatitude.getText().toString().trim();
        String longitudeStr = editTextLongitude.getText().toString().trim();

        if (!latitudeStr.isEmpty() && !longitudeStr.isEmpty()) {
            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
                    mapView.getController().setCenter(new GeoPoint(latitude, longitude));
                    mapView.getController().setZoom(15.0);
                } else {
                    Toast.makeText(this, "Latitud o longitud fuera de rango", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese latitud y longitud", Toast.LENGTH_SHORT).show();
        }
    }
}