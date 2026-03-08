package ChefZoneBackend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    
    @Autowired
    private Cloudinary cloudinary;
    
    public String uploadFile(MultipartFile file, String folderName) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }
            
            // Convertir MultipartFile a File temporal
            File convertedFile = convertMultipartFileToFile(file);
            
            // Subir a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                convertedFile,
                ObjectUtils.asMap(
                    "folder", "chefzone/" + folderName,
                    "resource_type", "auto"
                )
            );
            
            // Eliminar archivo temporal
            convertedFile.delete();
            
            // Devolver URL segura
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a Cloudinary: " + e.getMessage());
        }
    }
    
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
    
    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    
    public String extractPublicIdFromUrl(String url) {
        // Ejemplo: https://res.cloudinary.com/demo/image/upload/v123456/chefzone/users/nombre.jpg
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        String publicId = publicIdWithExtension.split("\\.")[0];
        return "chefzone/users/" + publicId; // Ajusta según tu estructura de carpetas
    }
}