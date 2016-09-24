package name.javalex.kiddraw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

import name.javalex.kiddraw.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private float smallBrush, mediumBrush, largeBrush;
    private DrawView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        drawView = (DrawView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawBtn = (ImageButton)findViewById(R.id.btn_brush);
        drawBtn.setOnClickListener(this);

        eraseBtn = (ImageButton)findViewById(R.id.btn_eraser);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.btn_new);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);
/*

        undoBtn = (ImageButton)findViewById(R.id.btn_undo);
        undoBtn.setOnClickListener(this);

        redoBtn = (ImageButton)findViewById(R.id.btn_redo);
        redoBtn.setOnClickListener(this);
*/

        drawView.setBrushSize(mediumBrush);
    }

    public void paintClicked(View view){
        drawView.setErase(false);
        //use chosen color
        if(view!=currPaint){
        //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            drawView.setBrushSize(drawView.getLastBrushSize());
            currPaint=(ImageButton)view;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_brush: {
                //draw button clicked
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle(R.string.dialog_brush_title);
                brushDialog.setContentView(R.layout.brush_chooser);

                ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });

                ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });

                ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });

                brushDialog.show();
                break;
            }
            case R.id.btn_eraser: {
                //switch to erase - choose size
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle(R.string.dialog_eraser_title);
                brushDialog.setContentView(R.layout.brush_chooser);

                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });

                brushDialog.show();
                break;
            }
            case R.id.btn_new: {
                //new button
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(R.string.dialog_new_title);
                newDialog.setMessage(R.string.dialog_new_message);
                newDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;
            }
            case R.id.btn_save: {
                //save drawing
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle(R.string.dialog_save_title);
                saveDialog.setMessage(R.string.dialog_save_message);
                saveDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //save drawing

                        drawView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");
                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    R.string.toast_saved, Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    R.string.toast_cannot_save, Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });

                saveDialog.show();
                break;
            }
/*
            case R.id.btn_undo: {
                drawView.onClickUndo();
                break;
            }

            case R.id.btn_redo: {
                drawView.onClickRedo();
                break;
            }
            */
        }
//        if(view.getId()==R.id.btn_brush){
//
//
//        } else if(view.getId()==R.id.btn_eraser){
//
//
//        } else if(view.getId()==R.id.btn_new){
//
//        } else if(view.getId()==R.id.btn_save){
//
//
//        }
    }

}
