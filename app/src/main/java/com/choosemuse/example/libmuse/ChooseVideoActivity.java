package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by christoph on 23.04.17.
 */

public class ChooseVideoActivity extends Activity{

    ListView list;
    String[] web = {
            "Hackaburg Teaser",
            "Guardians of the Galaxy 2",
            "Star Wars"
    };
    Integer[] imageId = {
            R.mipmap.hackaburg,
            R.mipmap.guardians,
            R.mipmap.starwars

    };
    String[] idArray = {
            "9_r0pqmMHtY",
            "QPX2jfWru-A",
            "wa8bFzzKebg"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_video);

        CustomList adapter = new
                CustomList(ChooseVideoActivity.this, web, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ChooseVideoActivity.this, VideoActivity.class);
                intent.putExtra("id", idArray[(int) id]);
                startActivity(intent);

            }
        });

    }
}