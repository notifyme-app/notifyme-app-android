package ch.ubique.notifyme.app.qr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {

	private final static String TAG = QrCodeAnalyzer.class.getCanonicalName();

	private Listener listener;
	private Context context;
	private YuvToRgbConverter yuvToRgbConverter;

	public QrCodeAnalyzer(Listener listener, Context context) {
		this.listener = listener;
		this.context = context;
		this.yuvToRgbConverter = new YuvToRgbConverter(context);
	}

	@SuppressLint("UnsafeExperimentalUsageError")
	@Override
	public void analyze(@NonNull ImageProxy image) {

		int width = image.getWidth();
		int height = image.getHeight();
		int ySize = width * height;

		//ImageProxy.PlaneProxy[] p = image.getPlanes();

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(width, height, conf); // this creates a MUTABLE bitmap
		yuvToRgbConverter.yuvToRgb(image.getImage(), bmp);
		//byte[] imageData = new byte[ySize];

		byte[] red = new byte[ySize];
		int[] pixels = new int[ySize];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < ySize; i++) {
			red[i] = (byte) ((pixels[i] & 0x0000ff00) >> 8);
		}

		/*
		ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();

		int rowStride = image.getPlanes()[0].getRowStride();
		int pos = 0;

		if (rowStride == width) { // likely
			yBuffer.get(imageData, 0, ySize);
			pos += ySize;
		} else {
			int yBufferPos = -rowStride; // not an actual position
			for (; pos < ySize; pos += width) {
				yBufferPos += rowStride;
				yBuffer.position(yBufferPos);
				yBuffer.get(imageData, pos, width);
			}
		}

		 */

		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
				red,
				image.getWidth(), image.getHeight(),
				0, 0,
				image.getWidth(), image.getHeight(),
				false
		);

		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new QRCodeMultiReader().decode(binaryBitmap);
			checkQrCode(result);
		}
		// Catch all kinds of dubious exceptions that zxing throws
		catch (FormatException e) {
			Log.w(TAG, "Caught FormatException");
		} catch (ChecksumException e) {
			Log.w(TAG, "Caught ChecksumException");
		} catch (NotFoundException e) {
			// Do nothing
			listener.noQRCodeFound();
		} finally {
			// Must be called else new images won't be received or camera may stall (depending on back pressure setting)
			image.close();
		}
	}

	private void checkQrCode(Result qrCode) {
		if (qrCode != null) {
			listener.onQRCodeFound(qrCode.getText());
		} else {
			listener.noQRCodeFound();
		}
	}

	public interface Listener {
		void onQRCodeFound(String qrCodeData);

		void noQRCodeFound();

	}

}
