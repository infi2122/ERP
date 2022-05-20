package Readers;

import Models.rawPiece;
import Models.supplier;
import Readers.xmlRawPiecesReader;
import Readers.xmlSuppliersReader;

import java.util.ArrayList;

public class suppliersList {

    public ArrayList<supplier> availableSuppliers() {

        ArrayList<rawPiece> pieces = availableRawPieces();
        xmlSuppliersReader reader = new xmlSuppliersReader();
        ArrayList<supplier> suppliers = reader.readSuppliers();
        int i = 0;
        for(supplier curr : suppliers ){
            curr.addNewRawPiece(pieces.get(i));
            curr.addNewRawPiece(pieces.get(i+1));
            i+=2;
        }
        return suppliers;

    }

    protected ArrayList<rawPiece> availableRawPieces() {

        xmlRawPiecesReader reader = new xmlRawPiecesReader();
        return reader.readRawPieces();

    }

}
