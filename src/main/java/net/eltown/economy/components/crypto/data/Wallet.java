package net.eltown.economy.components.crypto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Wallet {

    private String owner;
    private double ctc, elt, not;

}
