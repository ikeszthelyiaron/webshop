package hu.progmasters.webshop.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class S3Service {
    private final AmazonS3 s3Client; //kell az Amazon s3 szolgáltatás eléréséhez. Minden felhős művelet ezen keresztül megy
    private final String bucketName; //ebben fogjuk tárolni az adatokat. Amazonon kézzel hoztam létre

    // YAML-ből beállítja a Key-eket, a bucketet, és a régiót, ez az S3Service konstruktora ETTŐL
    public S3Service(
            @org.springframework.beans.factory.annotation.Value("${aws.s3.access-key}") String accessKey,
            @org.springframework.beans.factory.annotation.Value("${aws.s3.secret-key}") String secretKey,
            @org.springframework.beans.factory.annotation.Value("${aws.s3.bucket-name}") String bucketName,
            @org.springframework.beans.factory.annotation.Value("${aws.s3.region}") String region) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey); //AWS SDK biztosítja, ez hozza létre a hitelesítési adatokat. Kell az a kapcsolathoz!

        this.s3Client = AmazonS3ClientBuilder.standard() //S3 példányt hozza létre, amin keresztül kommunikálunk az Amazon szolgáltatásával
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)) //a fentebbi awsCreds-nek megadjuk a hitelesítési adatokat
                .build();

        this.bucketName = bucketName;
    } //konstruktor EDDIG

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();//currentTimeMillis: ktuális időbélyeget (timestamp-et) adja hozzá a fájlnévhez. Ez biztosítja, hogy minden fájl egyedi nevet kapjon, még akkor is, ha több fájlt ugyanazzal a névvel töltünk fel.
//file.getOriginalFilename(): a MultipartFile-ből kinyerjük a feltöltött fájl eredeti nevét, például "image.png".
        //String fileName: ez lesz az egyedi neve a file-nak, ez kerül az S3 bucketbe a felhőbe

        ObjectMetadata metadata = new ObjectMetadata();//meta adatra van szüksége az S3-nak. Ez az osztály tartalmazza ezekt az adatokat
        metadata.setContentLength(file.getSize()); //pl. file méretét adjuk meg, MultipartFile getSize() metódusával
        metadata.setContentType(file.getContentType()); //pl.file típusát adjuk meg, MultipartFile getContentType() metódusával

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)); //ez végzi a tényleges file feltöltést az s3-ba, a file tartalmt InputStreamben adjuk át

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }


}
