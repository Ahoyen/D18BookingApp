package com.ptit.aird18bookingapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.ptit.aird18bookingapp.BuildConfig;
import com.ptit.aird18bookingapp.R;
import com.ptit.aird18bookingapp.activities.AboutActivity;
import com.ptit.aird18bookingapp.activities.BookedActivity;
import com.ptit.aird18bookingapp.activities.ChartActivity;
import com.ptit.aird18bookingapp.activities.LoginActivity;
import com.ptit.aird18bookingapp.activities.MainActivity;
import com.ptit.aird18bookingapp.adapters.HistoryBookingAdapter;
import com.ptit.aird18bookingapp.listeners.BookingResponseDetailListener;
import com.ptit.aird18bookingapp.models.BookedRoom;
import com.ptit.aird18bookingapp.models.BookingResponseDetail;
import com.ptit.aird18bookingapp.repository.RequestManager;
import com.ptit.aird18bookingapp.utils.Constants;
import com.ptit.aird18bookingapp.utils.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

    public class Settings2Fragment extends Fragment {
        LinearLayout lvl_chart, lvl_language, lvl_booked, lvl_pdf, lvl_contact, lvl_about;
        String LOCALE_VIETNAM = "vi";
        String LOCALE_ENGLISH = "en";
        Locale mLocale;
        TextView tvLanguage;
        TextView settings, bookingHistory, about, contact, language, chart, pdf, logout;

        RequestManager manager;
        private PreferenceManager preferenceManager;
        List<BookedRoom> bookingList = new ArrayList<>();

        String cookie;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_settings2, container, false);

            getViews(view);

            preferenceManager = new PreferenceManager(getContext());

            String lang = preferenceManager.getString("app_language");
            if (lang == null) lang = LOCALE_ENGLISH;

            mLocale = new Locale(lang);
            setLocale(mLocale);

            setEvent();

            manager = new RequestManager(getContext());
            cookie = preferenceManager.getString(Constants.KEY_COOKIE);
            manager.getBookingResponseDetail(bookingResponseDetailListener, cookie);

            return view;
        }
        private void setLocale(Locale locale) {
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
                getContext().createConfigurationContext(config);
            } else {
                config.locale = locale;
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            }

            preferenceManager.putString("app_language", locale.getLanguage());

            mLocale = locale;

            setText(locale);
        }
        private void restartFragment() {
            // Reload lại fragment để áp dụng ngôn ngữ mới
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        private void setEvent() {
            lvl_chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ChartActivity.class));
                }
            });

            lvl_language.setOnClickListener(view -> {
                if (LOCALE_VIETNAM.equals(mLocale.getLanguage())) {
                    setLocale(new Locale(LOCALE_ENGLISH));
                } else {
                    setLocale(new Locale(LOCALE_VIETNAM));
                }
                restartFragment();
            });

            lvl_booked.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), BookedActivity.class));
            });
            lvl_pdf.setOnClickListener(view -> {
                ActivityCompat.requestPermissions(getActivity(), new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PackageManager.PERMISSION_GRANTED);
                try {
                    createInvoice1(bookingList);
                    Toast.makeText(getContext(), "duoc", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getContext(), "khong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            });
            lvl_contact.setOnClickListener(view -> {
                Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tisn.ng.7/"));
                startActivity(open);
            });
            lvl_about.setOnClickListener(view -> {
                Intent open = new Intent(getActivity(), AboutActivity.class);
                startActivity(open);
            });
            logout.setOnClickListener(view -> {
                signOut();
            });
        }
        private void signOut() {
            preferenceManager.clear();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        private void setText(Locale locale) {
            if (LOCALE_VIETNAM.equals(locale.getLanguage())) {
                settings.setText("Cài đặt");
                bookingHistory.setText("Lịch sử đặt phòng");
                about.setText("Về chúng tôi");
                contact.setText("Liên hệ");
                language.setText("Ngôn ngữ");
                chart.setText("Biểu đồ");
                pdf.setText("In PDF");
                logout.setText("Đăng xuất");
                tvLanguage.setText("Tiếng Việt");
            } else {
                settings.setText("Settings");
                bookingHistory.setText("Booking history");
                about.setText("About");
                contact.setText("Contact");
                language.setText("Language");
                chart.setText("Chart");
                pdf.setText("Print PDF");
                logout.setText("Logout");
                tvLanguage.setText("English");
            }
        }

        private void dialog(){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage(R.string.changLanguage);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    });

            builder1.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        private void getViews(View view) {
            lvl_language=view.findViewById(R.id.lvl_language);
            lvl_chart=view.findViewById(R.id.lvl_chart);
            lvl_booked=view.findViewById(R.id.lvl_booked);
            tvLanguage=view.findViewById(R.id.tvLanguage);
            lvl_pdf=view.findViewById(R.id.lvl_pdf);
            lvl_contact=view.findViewById(R.id.lvl_contact);
            lvl_about=view.findViewById(R.id.lvl_about);


            settings=view.findViewById(R.id.settings);
            bookingHistory=view.findViewById(R.id.bookingHistory);
            about=view.findViewById(R.id.about);
            contact=view.findViewById(R.id.contact);
            language=view.findViewById(R.id.language);
            chart=view.findViewById(R.id.chart);
            pdf=view.findViewById(R.id.pdf);
            logout=view.findViewById(R.id.logout);

        }
        private final BookingResponseDetailListener bookingResponseDetailListener = new BookingResponseDetailListener() {
            @Override
            public void didFetch(BookingResponseDetail response, String message) {
                if (response != null && response.data.bookedRooms != null) {
                    bookingList.clear();
                    bookingList.addAll(response.data.bookedRooms);

                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu lịch sử đặt phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void didError(String message) {
                //Toast.makeText(BookedActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };
        private void createInvoice1(List<BookedRoom> bookingList) throws IOException {
            int pageWidth = 1200;
            int pageHeight = 2010;

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            Bitmap bmpScale = Bitmap.createScaledBitmap(bmp, pageWidth, 518, false);

            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            Paint titlePaint = new Paint();
            Paint tablePaint = new Paint();

            int yStart = 600;
            int rowHeight = 60;
            int pageNumber = 1;
            PdfDocument.Page currentPage = null;
            Canvas canvas = null;

            int totalAmount = 0;
            int y = yStart;

            for (int i = 0; i < bookingList.size(); i++) {
                BookedRoom room = bookingList.get(i);
                if (currentPage == null || y + rowHeight > pageHeight) {
                    if (currentPage != null) {
                        pdfDocument.finishPage(currentPage);
                    }

                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create();
                    currentPage = pdfDocument.startPage(pageInfo);
                    canvas = currentPage.getCanvas();
                    drawHeader(canvas, bmpScale, paint, titlePaint, tablePaint, pageWidth, yStart);
                    y = yStart + 80 + 40; //khoảng cách giữa header và dòng dữ liệu
                }

                int rowTotal = (int)(room.numberOfDays * room.pricePerDay);
                totalAmount += rowTotal;

                // Vẽ nội dung dòng
                paint.setTextSize(35);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.BLACK);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                canvas.drawText(String.valueOf(i + 1), 40, y, paint);
                canvas.drawText(room.roomName, 130, y, paint);
                canvas.drawText(room.checkinDate, 430, y, paint);
                canvas.drawText(room.checkoutDate, 630, y, paint);
                canvas.drawText(String.valueOf(room.numberOfDays), 830, y, paint);
                canvas.drawText(String.valueOf(room.pricePerDay), 930, y, paint);
                canvas.drawText(String.valueOf(rowTotal), 1080, y, paint);
                y += rowHeight;
            }

            if (currentPage != null) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(45);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawLine(700, y + 20, pageWidth - 20, y + 20, paint);
                canvas.drawText("Tổng tiền:", 700, y + 90, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(totalAmount + " VND", pageWidth - 30, y + 90, paint);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Tổng số đơn: " + bookingList.size(), 700, y + 160, paint);
                pdfDocument.finishPage(currentPage);
            }

            // Lưu file PDF
            String folderPath = Environment.getExternalStorageDirectory().getPath() + "/documents";
            File folderFile = new File(folderPath);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            String filePath = folderPath + "/BookingInvoice_" + System.currentTimeMillis() + ".pdf";
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(getContext(), "Đã lưu file: " + filePath, Toast.LENGTH_LONG).show();
            openPdfViewer(file);
        }
        private void drawHeader(Canvas canvas, Bitmap bmpScale, Paint paint, Paint titlePaint, Paint tablePaint, int pageWidth, int yStart) {
            // Vẽ logo
            canvas.drawBitmap(bmpScale, 0, 0, paint);

            // Tiêu đề
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText("LỊCH SỬ ĐẶT PHÒNG", pageWidth / 2, 580, titlePaint);

            // Vẽ bảng header
            tablePaint.setStyle(Paint.Style.STROKE);
            tablePaint.setStrokeWidth(3);
            tablePaint.setColor(Color.BLACK);
            canvas.drawRect(20, yStart, pageWidth - 20, yStart + 80, tablePaint);

            tablePaint.setStyle(Paint.Style.FILL);
            tablePaint.setTextSize(35);
            tablePaint.setColor(Color.BLACK);

            canvas.drawText("STT", 40, yStart + 55, tablePaint);
            canvas.drawText("Tên phòng", 130, yStart + 55, tablePaint);
            canvas.drawText("Check-in", 430, yStart + 55, tablePaint);
            canvas.drawText("Check-out", 630, yStart + 55, tablePaint);
            canvas.drawText("Ngày", 830, yStart + 55, tablePaint);
            canvas.drawText("Giá/ngày", 930, yStart + 55, tablePaint);
            canvas.drawText("Tổng", 1080, yStart + 55, tablePaint);

            // Các đường kẻ dọc
            canvas.drawLine(110, yStart, 110, yStart + 80, tablePaint);
            canvas.drawLine(410, yStart, 410, yStart + 80, tablePaint);
            canvas.drawLine(610, yStart, 610, yStart + 80, tablePaint);
            canvas.drawLine(810, yStart, 810, yStart + 80, tablePaint);
            canvas.drawLine(910, yStart, 910, yStart + 80, tablePaint);
            canvas.drawLine(1060, yStart, 1060, yStart + 80, tablePaint);
        }

        private void openPdfViewer(File file) {
            Uri uri = FileProvider.getUriForFile(getContext(),
                    "com.ptit.aird18bookingapp.fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "Không tìm thấy ứng dụng để mở file PDF", Toast.LENGTH_LONG).show();
            }

        }
    }