package sysu.project.lee.sportslife.News.UI;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import sysu.project.lee.sportslife.News.Utils.AppConfig;
import sysu.project.lee.sportslife.News.Utils.HttpUtils;
import sysu.project.lee.sportslife.News.Utils.ImageUtils;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.ToastUtils;


public class ImageDialog extends Activity
{
	private ProgressBar progressBar;
    private String imageURL = null;
	private ImageView imageView;
    private Bitmap bmp = null;
    private final int SAVE_TO_PHONE = 0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_dialog);
		progressBar = (ProgressBar) findViewById(R.id.imagedialog_progress);
		imageView = (ImageView) findViewById(R.id.imagedialog_image);
		Intent intent = getIntent();
        imageURL = intent.getStringExtra("url");
		System.out.println(imageURL);
		
		new AsyncTask<String, Integer, Bitmap>()
		{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                imageView.setFocusable(false);
            }

            @Override
			protected void onPostExecute(Bitmap result)
			{
				if(result != null)
				{
					imageView.setImageBitmap(result);
                    imageView.setFocusable(true);
				}
				else
					Toast.makeText(ImageDialog.this, "下载失败，请重新载入", Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}

			@Override
			protected Bitmap doInBackground(String... params)
			{
				InputStream is = null;
				
				try
				{
					is = HttpUtils.getInputStream(params[0]);
					bmp = BitmapFactory.decodeStream(is);
					return bmp;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(is != null)
					{
						try
						{
							is.close();
							is = null;
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				return null;
			}

		}.execute(imageURL);

        imageView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, SAVE_TO_PHONE, 0, "保存到手机");
            }
        });

//        private boolean onContextItemSelected()


	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case SAVE_TO_PHONE:
                onSaveImageToSDCard(bmp);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void onSaveImageToSDCard(Bitmap bmp) {
        if (bmp != null){
            ImageUtils.saveImageToSD(bmp, imageURL);
            ToastUtils.show(ImageDialog.this, "图片已经保存到"+ AppConfig.APP_IMAGE_DIR);
        }
    }
}
