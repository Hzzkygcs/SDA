Semoga bisa membantu teman-teman semua juga :D

### FAQ

<details> <summary>click here to expand FAQ</summary>


- 7zip exception: The system cannot find the file specified

  Hal ini diduga disebabkan karena ada masalah permission untuk mendownload file `.7z`-nya ataupun masalah permission untuk memodifikasi folder tersebut. Salah satu cara yang pernah berhasil adalah men-download dan meng-extract HzzGrader pada direktori `C:\Users\<user name>\Downloads`. Cara lainnya adalah dengan mencoba menjalankan HzzGrader dengan *administrator privilege*

- java --version, javac --version, dan JNI error

  Hal ini biasanya disebabkan karena versi java yang sudah terinstall di laptop teman-teman perlu diupdate. Bisa juga karena ada miskonfigurasi pada instalasi JDK teman-teman. Solusinya bisa dilihat [di sini](https://github.com/Hzzkygcs/SDA/blob/master/.misc/java%20--version%20error.md)

- IOException

  Teman-teman coba periksa kembali apakah ada `final` pada variabel IO (seperti `in` atau `out` misalnya). Jika ada, teman-teman bisa menghapus keyword final pada variabel tersebut.

- Mengubah batas waktu TLE

  Untuk mengubah batas waktu TLE, teman-teman bisa mengubah isi file `.configurations\time_limit_in_ms.txt` dengan suatu bilangan bulat. Pastikan file hanya terdiri atas **1 baris**, dan **tidak ada karakter spasi maupun newline** di dalamnya.

- Output program berbeda dengan output pada VSCode/Intellij

  Jika hal ini terjadi, coba inisiasikan semua static variable pada awal-awal fungsi main(). Misal jika kita punya:  `public static int my_variable = 3;`, maka tambahkan: 

  ```java
  public static void main(String[] args) {
  	my_variable = 3;
  	// kode anda
  }
  ```

- Stuck di "parsing and wrapping your copied ..."  (solved in v1.3)

  Biasanya ini karena HzzGrader tidak bisa membaca/menulis ke folder `HzzGrader/bin/`. Merestart laptop atau kill process `java.exe` dan `javaw.exe` biasanya menjadi solusi umum. Pastikan juga tidak ada dua/lebih window HzzGrader yang terbuka secara bersamaan.

- Membuka file log.txt

  File log.txt dapat dibuka dengan cara klik kanan pada tulisan/logo HzzGrader (di pojok kiri atas)



</details>





## Download HzzGrader

Teman-teman bisa mendownload versi terbaru aplikasinya [disini](https://github.com/Hzzkygcs/SDA/releases)

### Cara memakai

#### versi 1.2 atau ke atas

Teman-teman cukup mendownload zip aplikasinya (versi 1.2) [disini](https://github.com/Hzzkygcs/SDA/releases). Disarankan mendownload yang no auto update. Setelah itu tinggal meng-extract zip-nya dan menjalankan programnya. Setelah itu tinggal pilih source code java yang mau diuji dan memilih testcase yang ingin digunakan. Kalau sudah, tunggu sebentar sampai HzzGrader selesai mendownload testcase. Kalau sudah selesai download, tinggal tekan `Start Test`.

#### versi 1.0

Cara memakai-nya cukup mudah. Teman-teman cukup mendownload dan menginstall aplikasinya [disini](https://github.com/Hzzkygcs/SDA/releases). Setelah itu, teman-teman juga cukup mendownload file testcase (dalam format rar) yang ingin di-download. Extract testcasenya pada suatu folder. Jalankan aplikasi HzzGrader, kemudian pilih lokasi kode java dan pilih lokasi folder testcase tadi. Kalau sudah, tinggal tekan `Start Test`.

### masalah untuk yang auto update

Ketika mendownload yang *auto update*, beberapa antivirus mungkin akan menganggap virus. Kadang juga saat mau menginstall update, instalasinya diblokir oleh antivirus. Namun sebenarnya teman-teman tidak perlu khawatir. Program yang di-upload ini transparan dan source-codenya dapat dilihat pada folder `GUI Version/`. Jadi teman-teman bisa menambahkan whitelist pada antivirus untuk HzzGrader ini. Tapi teman-teman juga tetap dibebaskan memilih versi yang tidak diimplementasikan auto update.



## Donation

Kalau teman-teman merasa aplikasi ini sangat berguna dan ingin memberikan sedikit donasi, teman-teman bisa mengirimkannya ke [https://saweria.co/HzzHzz](https://saweria.co/HzzHzz) 
Terima kasih atas dukungan teman-teman semua :D



## Cross-platform Alternative

Untuk teman-teman yang tidak menggunakan windows, teman-teman bisa mengunjungi website dengan fungsionalitas yang serupa, yakni [chronojudge](https://chronojudge.netlify.app/)! Have a nice day! :D

