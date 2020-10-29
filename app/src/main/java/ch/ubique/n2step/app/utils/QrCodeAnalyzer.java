package ch.ubique.n2step.app.utils;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {

	private final static String TAG = QrCodeAnalyzer.class.getCanonicalName();

	private Listener listener;

	public QrCodeAnalyzer(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void analyze(@NonNull ImageProxy image) {
		ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
		byte[] imageData = new byte[byteBuffer.capacity()];
		byteBuffer.get(imageData);

		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
				imageData,
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
			e.printStackTrace();
		} catch (ChecksumException e) {
			Log.w(TAG, "Caught ChecksumException");
			e.printStackTrace();
		} catch (NotFoundException e) {
			// Do nothing
		} finally {
			// Must be called else new images won't be received or camera may stall (depending on back pressure setting)
			image.close();
		}
	}

	private void checkQrCode(Result qrCode) {
		//TODO check QR Code format and covert to QrCodeData object
		if (qrCode != null) {
			listener.onQRCodeFound(qrCode.getText());
		}
	}

	public interface Listener {
		void onQRCodeFound(String qrCodeData);

	}

}
