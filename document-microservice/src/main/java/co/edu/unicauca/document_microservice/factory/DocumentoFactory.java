package co.edu.unicauca.document_microservice.factory;

import co.edu.unicauca.document_microservice.utilities.validator.DocumentoValidator;
import co.edu.unicauca.document_microservice.utilities.validator.PdfValidator;

public class DocumentoFactory {

    private static volatile DocumentoFactory instance;

    private DocumentoFactory() {}

    public static DocumentoFactory getInstance() {
        if (instance == null) {
            synchronized (DocumentoFactory.class) {
                if (instance == null) {
                    instance = new DocumentoFactory();
                }
            }
        }
        return instance;
    }

    public DocumentoValidator crearValidator(String tipoDocumento) {
        switch (tipoDocumento) {
            case "FORMATO_A":
            case "ANTEPROYECTO":
            case "CARTA_EMPRESA":
            case "MONOGRAFIA":
            case "ANEXOS":
            case "PRESENTACION":
                return new PdfValidator();
            default:
                throw new IllegalArgumentException("Tipo de documento no soportado: " + tipoDocumento);
        }
    }
}