# Mengatasi java --version  atau javac --version error



1. Download dan install Java SE 15 (atau yang terbaru). Link website resmi untuk mendownload: [link ini](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html)

2. Dapatkan path instalasi Java SE 15-nya (direktori bin). Bisa menggunakan perintah `where java` pada cmd, maupun menggunakan cara-cara lainnya. 

   contohnya adalah: `C:\Program Files\Java\jdk-15.0.2\bin\`

3.  Buka environment variables

   <img src="https://github.com/Hzzkygcs/SDA/blob/master/.misc/img/java%20--version%20error/img1.png?raw=true" alt="langkah-langkah" style="height:400px;" />

4. Tambahkan path dari Java SE tersebut ke dalam variabel `Path` sesuai langkah-langkah di bawah ini

   <img src="https://github.com/Hzzkygcs/SDA/blob/master/.misc/img/java%20--version%20error/img2.png?raw=true" alt="langkah-langkah" style="height:400px;" />

5. Setelah itu pindahkan direktori Java SE tersebut ke paling atas

   <img src="https://github.com/Hzzkygcs/SDA/blob/master/.misc/img/java%20--version%20error/img3.png?raw=true" alt="langkah-langkah" style="height:400px;" />

6. Lakukan hal yang sama untuk system variables

   <img src="https://github.com/Hzzkygcs/SDA/blob/master/.misc/img/java%20--version%20error/img4.png?raw=true" alt="langkah-langkah" style="height:400px;" />
   

7. Selesai!  Tinggal menutup `HzzGrader`, lalu dibuka lagi. Seharusnya jika langkah sudah dijalankan dengan benar, HzzGrader akan berhasil meng-compile kode java teman-teman. 

   Selamat mencoba! :D



