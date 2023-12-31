package com.example.hackerthon;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.opencsv.CSVReader;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.gen.KakaoMapLibraryAndroidMeta;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {
    private static final String LOG_TAG = "MainActivity";
    MapView mMapView;

    MapPOIItem marker = new MapPOIItem();
    Random random = new Random();

    String Title = "Merge";

    private static final String FILE_Convin = "Convenience.csv";
    private static final String FILE_Food = "Food.csv";
    private static final String FILE_Guitar = "Guitar.csv";
    private static final String FILE_Mart = "Mart.csv";
    private static final String FILE_Merge = "merge.csv";


    List<String[]> dataList_Convin;
    List<String[]> dataList_Food;
    List<String[]> dataList_Guitar;
    List<String[]> dataList_Mart;
    List<String[]> dataList_Merge;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AssetManager assetManager = this.getAssets();

        dataList_Convin = CSVGetter(assetManager, FILE_Convin);
        dataList_Food = CSVGetter(assetManager, FILE_Food);
        dataList_Guitar = CSVGetter(assetManager, FILE_Guitar);
        dataList_Mart = CSVGetter(assetManager, FILE_Mart);
        dataList_Merge = CSVGetter(assetManager, FILE_Merge);

        //mapview
        mMapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);
        //mMapView = (MapView) findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setMapViewEventListener(this);
        //

        if (!checkLocationServicesStatus())
            showDialogForLocationServiceSetting();
        else
            checkRunTimePermission();

        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        //마커
        SetMarker();
        /*for (String[] data : dataList_Guitar) {
            if (data[8].equals("y")) continue;
            if (data[0].equals("")) break;
            if (Integer.parseInt(data[0]) >= 2)//index
            {
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(data[8]), Double.parseDouble(data[7]));
                marker.setItemName(data[2]);
                marker.setTag(1);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 사설 마커 모양 - 기본
                marker.setCustomImageResourceId(R.drawable.mar); // 마커 이미지.
                marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                //marker.setCustomImageAnchor(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커// 모양.
                marker.setCustomSelectedImageResourceId(R.drawable.mar2); // 마커 이미지.
                //marker.setCustomSelec(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

                mMapView.addPOIItem(marker);
                Log.d("jhdroid_test", "data : " + Arrays.deepToString(data));
            }
        }*/

        //검색 ///////////////////////////////////////////////

        //파일 입력
        //List<String[]> mergeData = readFile("merge.csv");

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText text = findViewById(R.id.searchData);
        Button searchBtn = findViewById(R.id.buttonforSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.removeAllPOIItems();

                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                //Toast.makeText(MainActivity.this, text.getText(), Toast.LENGTH_LONG).show();//alert
                String toFind = String.valueOf(text.getText());
                System.out.println(toFind);
                if (toFind.equals("")) {
                    Toast.makeText(MainActivity.this, "검색어를 입력해주세요", Toast.LENGTH_LONG).show();//alert
                    return;
                }
                List<String[]> foundList = new ArrayList<>();
                for (String[] dataRow : dataList_Merge) {
                    if (dataRow[2].contains(toFind)) {
                        System.out.println(dataRow[1] + " " + dataRow[2]);
                        foundList.add(dataRow);
                    }
                }
                if (foundList.size() == 0)
                    Toast.makeText(MainActivity.this, "검색어와 일치하는 가맹점이 없습니다", Toast.LENGTH_LONG).show();//alert
                else {
                    for (String[] s : foundList) {
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(s[8]), Double.parseDouble(s[7]));
                        marker.setItemName(s[2]);
                        marker.setTag(1);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 사설 마커 모양 - 기본
                        marker.setCustomImageResourceId(R.drawable.mar); // 마커 이미지.
                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        //marker.setCustomImageAnchor(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커// 모양.
                        marker.setCustomSelectedImageResourceId(R.drawable.mar2); // 마커 이미지.
                        //marker.setCustomSelec(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

                        mMapView.addPOIItem(marker);
                    }
                    if (foundList.size() == 1) {
                        MapPoint mp = MapPoint.mapPointWithGeoCoord(Double.parseDouble(foundList.get(0)[8]), Double.parseDouble(foundList.get(0)[7]));
                        mMapView.moveCamera(CameraUpdateFactory.newMapPoint(mp));
                    }
                }
            }
        });


        //왼쪽
        // 전체화면인 DrawerLayout 객체 참조
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        // Drawer 화면(뷰) 객체 참조
        final View drawerViewLeft = (View) findViewById(R.id.drawerLeft);
        // 드로어 화면을 열고 닫을 버튼 객체 참조
        Button btnOpenDrawerLeft = (Button) findViewById(R.id.btn_OpenDrawerLeft);

        // 드로어 여는 버튼 리스너
        btnOpenDrawerLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                drawerLayout.openDrawer(drawerViewLeft);
            }
        });
    }

    //아임샵 아이콘 클릭했을 때
    public void onButton1Clicked(View v) {
        String packageName = "kr.co.dgb.dgbmh&gl=IE";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String url = "market://details?id=" + packageName;
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        }
    }

    public void Clickrestaurant(View v) {
        Title = "Food";
        SetMarker();
    }

    public void ClickNear(View v) {
        Title = "Merge";
        SetMarker();
    }

    public void ClickMart(View v) {
        Title = "Mart";
        SetMarker();
    }

    public void ClickFacility(View v) {
        Title = "Convin";
        SetMarker();
    }

    public void Clickguitar(View v) {
        Title = "Guitar";
        SetMarker();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!rect.contains(x, y)) {
                if (imm != null) imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //Intent intent = new Intent(this, StartActivity.class);
    //startActivity(intent);
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /*
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
     */

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음 - 현재 나침반은 고정
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
            }
        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
            //Toast.makeText(MainActivity.this, "가지고 있네요?", Toast.LENGTH_LONG).show();
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        Log.d("setzoomtest", Integer.toString(mMapView.getZoomLevel()));

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }


    //CSVGetter
    public static List<String[]> CSVGetter(AssetManager manager, String FILENAME) {
        List<String[]> dataList;
        try {
            InputStream inputStream = manager.open(FILENAME);
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            dataList = reader.readAll();
        } catch (IOException e) {
            dataList = new ArrayList<String[]>();
            dataList.add(new String[]{"error", "-1"});
            e.printStackTrace();
        }

        return dataList;
    }

    public void SetMarker() {
        List<String[]> dataList = null;
        int temp = 0;
        int count = 0;

        mMapView.removeAllPOIItems();
        if (Title == "Food") {
            dataList = dataList_Food;
            temp = 20;
        } else if (Title == "Mart") {
            dataList = dataList_Mart;
            temp = 20;
        } else if (Title == "Merge") {
            dataList = dataList_Merge;
            temp = 20;

        } else if (Title == "guitar") {
            dataList = dataList_Guitar;
            temp = 1;
        } else {
            dataList = dataList_Convin;
            temp = 5;
        }
        System.out.print(dataList);
        for (int i = 0; i < dataList.size(); i++) {
            if ( dataList.get(i)[8].equals("y") ) continue;
            if (dataList.get(i)[0].equals("")) break;
            if (Integer.parseInt(dataList.get(i)[0]) >= 2)//index
            {
                if (count > 150) break;
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(dataList.get(i)[8]), Double.parseDouble(dataList.get(i)[7]));
                marker.setItemName(dataList.get(i)[2]);
                marker.setTag(1);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 사설 마커 모양 - 기본
                marker.setCustomImageResourceId(R.drawable.mar); // 마커 이미지.
                marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                //marker.setCustomImageAnchor(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커// 모양.
                marker.setCustomSelectedImageResourceId(R.drawable.mar2); // 마커 이미지.
                //marker.setCustomSelec(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

                mMapView.addPOIItem(marker);
                count++;
                i += random.nextInt(temp) + 1;
            }
        }
    }
}
//마커
        /*for (String[] data : dataList) {
            if (data[8].equals("y")) continue;
            if (data[0].equals("")) break;
            if (Integer.parseInt(data[0]) >= 2)//index
            {
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(data[8]), Double.parseDouble(data[7]));
                marker.setItemName(data[2]);
                marker.setTag(1);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 사설 마커 모양 - 기본
                marker.setCustomImageResourceId(R.drawable.mar); // 마커 이미지.
                marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                //marker.setCustomImageAnchor(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커// 모양.
                marker.setCustomSelectedImageResourceId(R.drawable.mar2); // 마커 이미지.
                //marker.setCustomSelec(0.5f, 0.7f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

                mMapView.addPOIItem(marker);
                Log.d("jhdroid_test", "data : " + Arrays.deepToString(data));
            }
        }*/