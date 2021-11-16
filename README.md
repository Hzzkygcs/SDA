Semoga bisa membantu teman-teman semua juga :D


<details> <summary><h2>FAQ</h2></summary>


- IOException

  Teman-teman coba periksa kembali apakah ada `final` pada variabel IO (seperti `in` atau `out` misalnya). Jika ada, teman-teman bisa menghapus keyword final pada variabel tersebut.

- Stuck di "parsing and wrapping your copied ..."

  Biasanya ini karena HzzGrader tidak bisa membaca/menulis ke folder `HzzGrader/bin/`. Merestart laptop atau kill process `java.exe` dan `javaw.exe` biasanya menjadi solusi umum. Pastikan juga tidak ada dua/lebih window HzzGrader yang terbuka secara bersamaan.

- Membuka file log.txt

  File log.txt dapat dibuka dengan cara klik kanan pada tulisan/logo HzzGrader (di pojok kiri atas)
  
- Program dijalankan melalui HzzGrader hasilnya berbeda dari VSCode/Intellij

  Jika hal ini terjadi, coba inisiasikan semua static variable pada awal-awal fungsi main(). Misal jika kita punya:  `public static int my_variable;`, maka tambahkan: 

  ```java
  public static void main(String[] args) {
  	my_variable = 0;
  	// kode anda
  }
  ```
  

</details>





## Download HzzGrader

Teman-teman bisa mendownload versi terbaru aplikasinya [disini](https://github.com/Hzzkygcs/SDA/releases)

### Cara memakai

#### versi 1.2

Teman-teman cukup mendownload zip aplikasinya (versi 1.2) [disini](https://github.com/Hzzkygcs/SDA/releases). Disarankan mendownload yang no auto update. Setelah itu tinggal meng-extract zip-nya dan menjalankan programnya. Setelah itu tinggal pilih source code java yang mau diuji dan memilih testcase yang ingin digunakan. Kalau sudah, tunggu sebentar sampai HzzGrader selesai mendownload testcase. Kalau sudah selesai download, tinggal tekan `Start Test`.

#### versi 1.0

Cara memakai-nya cukup mudah. Teman-teman cukup mendownload dan menginstall aplikasinya [disini](https://github.com/Hzzkygcs/SDA/releases). Setelah itu, teman-teman juga cukup mendownload file testcase (dalam format rar) yang ingin di-download. Extract testcasenya pada suatu folder. Jalankan aplikasi HzzGrader, kemudian pilih lokasi kode java dan pilih lokasi folder testcase tadi. Kalau sudah, tinggal tekan `Start Test`.

### masalah untuk yang auto update

Ketika mendownload yang *auto update*, beberapa antivirus mungkin akan menganggap virus. Kadang juga saat mau menginstall update, instalasinya diblokir oleh antivirus. Namun sebenarnya teman-teman tidak perlu khawatir. Program yang di-upload ini transparan dan source-codenya dapat dilihat pada folder `GUI Version/`. Jadi teman-teman bisa menambahkan whitelist pada antivirus untuk HzzGrader ini. Tapi teman-teman juga tetap dibebaskan memilih versi yang tidak diimplementasikan auto update.



## Donation

Kalau teman-teman merasa aplikasi ini sangat berguna dan ingin memberikan sedikit donasi, teman-teman bisa mengirimkannya ke [https://saweria.co/HzzHzz](https://saweria.co/HzzHzz) 
Terima kasih atas dukungan teman-teman semua :D