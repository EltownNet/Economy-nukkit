package net.eltown.economy.components.shops.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import lombok.RequiredArgsConstructor;
import net.eltown.economy.Economy;
import net.eltown.economy.components.economy.language.Language;
import net.eltown.economy.components.forms.custom.CustomForm;
import net.eltown.economy.components.forms.modal.ModalForm;
import net.eltown.economy.components.forms.simple.SimpleForm;
import net.eltown.economy.components.shops.api.ShopAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ShopForms {

    private final ShopAPI shopAPI;

    private void openItemShop(final Player player, final int[] id) {
        final CustomForm customForm = new CustomForm.Builder("§7» §8" + Item.get(id[0], id[1]).getName())
                .addElement(new ElementInput("Bitte gebe an, wie viel du von diesem Item kaufen möchtest.", "64", "64"))
                .onSubmit((g, h) -> {
                    int i = Integer.parseInt(h.getInputResponse(0));

                    this.shopAPI.getCurrentPrice(id, i, finalPrice -> {
                        final ModalForm modalForm = new ModalForm.Builder("§7» §8Kaufbestätigung", "Möchtest du §a" + i + "x " + Item.get(id[0], id[1]).getName() + " §ffür"
                                + " §a$" + Economy.getAPI().getMoneyFormat().format(finalPrice) + " §fkaufen?", "§7» §aKaufen", "§7» §cAbbrechen")
                                .onYes(l -> {
                                    if (!player.getInventory().canAddItem(Item.get(id[0], id[1], i))) {
                                        player.sendMessage(Language.get("item.inventory.full"));
                                        this.playSound(player, Sound.NOTE_BASS);
                                        return;
                                    }

                                    Economy.getAPI().getMoney(player, money -> {
                                        if (money >= finalPrice) {
                                            Economy.getAPI().reduceMoney(player, finalPrice);
                                            this.shopAPI.sendBought(id, i);
                                            player.getInventory().addItem(Item.get(id[0], id[1], i));
                                            player.sendMessage(Language.get("item.bought", i, Item.get(id[0], id[1], i).getName(), Economy.getAPI().getMoneyFormat().format(finalPrice)));
                                            this.playSound(player, Sound.RANDOM_LEVELUP);
                                        } else {
                                            player.sendMessage(Language.get("item.not.enough.money"));
                                            this.playSound(player, Sound.NOTE_BASS);
                                        }
                                    });
                                })
                                .onNo(l -> {
                                    this.playSound(player, Sound.NOTE_BASS);
                                })
                                .build();
                        modalForm.send(player);
                    });
                })
                .build();
        customForm.send(player);
    }

    private final List<int[]> woodShop = new ArrayList<>(Arrays.asList(
            new int[]{17, 0}, new int[]{17, 1}, new int[]{17, 2},
            new int[]{17, 3}, new int[]{162, 0}, new int[]{162, 1}
    ));

    public void openWoodShop(final Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§7» §8Holzfäller Darick", "§7Wähle eines der aufgelisteten Items aus, welches du kaufen möchtest.");
        this.woodShop.forEach(id -> {
            this.shopAPI.getCurrentPrice(id, 1, price -> {
                form.addButton(new ElementButton(Item.get(id[0], id[1]).getName() + "\n§2§l1x §r§f$" + Economy.getAPI().getMoneyFormat().format(price), new ElementButtonImageData("url", "http://45.138.50.23:3000/img/shopitems/" + id[0] + "-" + id[1] + ".png")), e -> {
                    this.openItemShop(e, id);
                });
            });
        });
        form.build().send(player);
    }

    private final List<int[]> miningShop = new ArrayList<>(Arrays.asList(
            new int[]{15, 0}, new int[]{14, 0}, new int[]{264, 0},
            new int[]{351, 4}, new int[]{331, 0}, new int[]{263, 0}, new int[]{388, 0}
    ));

    public void openMiningShop(final Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§7» §8Minenarbeiter Patrick", "§7Wähle eines der aufgelisteten Items aus, welches du kaufen möchtest.");
        this.miningShop.forEach(id -> {
            this.shopAPI.getCurrentPrice(id, 1, price -> {
                form.addButton(new ElementButton(Item.get(id[0], id[1]).getName() + "\n§b§l1x §r§f$" + Economy.getAPI().getMoneyFormat().format(price), new ElementButtonImageData("url", "http://45.138.50.23:3000/img/shopitems/" + id[0] + "-" + id[1] + ".png")), e -> {
                    this.openItemShop(e, id);
                });
            });
        });
        form.build().send(player);
    }

    private final List<int[]> exploringShop = new ArrayList<>(Arrays.asList(
            new int[]{38, 0}, new int[]{37, 0}, new int[]{38, 3}, new int[]{175, 0}, new int[]{-216, 0},
            new int[]{38, 1}, new int[]{38, 2}, new int[]{38, 4}, new int[]{38, 5}, new int[]{38, 6}, new int[]{38, 7},
            new int[]{38, 8}, new int[]{38, 9}, new int[]{38, 10}, new int[]{32, 0}, new int[]{111, 0}, new int[]{-163, 0}
    ));

    public void openExploringShop(final Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§7» §8Reisende Maya", "§7Wähle eines der aufgelisteten Items aus, welches du kaufen möchtest.");
        this.exploringShop.forEach(id -> {
            this.shopAPI.getCurrentPrice(id, 1, price -> {
                form.addButton(new ElementButton(Item.get(id[0], id[1]).getName() + "\n§e§l1x §r§f$" + Economy.getAPI().getMoneyFormat().format(price), new ElementButtonImageData("url", "http://45.138.50.23:3000/img/shopitems/" + id[0] + "-" + id[1] + ".png")), e -> {
                    this.openItemShop(e, id);
                });
            });
        });
        form.build().send(player);
    }

    private final List<int[]> netherShop = new ArrayList<>(Arrays.asList(
            new int[]{-225, 0}, new int[]{-226, 0}, new int[]{214, 0}, new int[]{-227, 0}, new int[]{-232, 0},
            new int[]{-233, 0}, new int[]{-228, 0}, new int[]{-229, 0}, new int[]{-230, 0}, new int[]{-234, 0},
            new int[]{-236, 0}, new int[]{88, 0}, new int[]{-273, 0}, new int[]{-289, 0}, new int[]{372, 0}
    ));

    public void openNetherShop(final Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§7» §8Netherexpertin Lilly", "§7Wähle eines der aufgelisteten Items aus, welches du kaufen möchtest.");
        this.netherShop.forEach(id -> {
            this.shopAPI.getCurrentPrice(id, 1, price -> {
                form.addButton(new ElementButton(Item.get(id[0], id[1]).getName() + "\n§4§l1x §r§f$" + Economy.getAPI().getMoneyFormat().format(price), new ElementButtonImageData("url", "http://45.138.50.23:3000/img/shopitems/" + id[0] + "-" + id[1] + ".png")), e -> {
                    this.openItemShop(e, id);
                });
            });
        });
        form.build().send(player);
        // 5Hf8qgiyxHtejGqzBoG85fi8KRhrY5eBGjRXAqhg
    }

    private final List<int[]> mobdropShop = new ArrayList<>(Arrays.asList(
            new int[]{289, 0}, new int[]{367, 0}, new int[]{287, 0}, new int[]{399, 0}, new int[]{368, 0},
            new int[]{341, 0}, new int[]{376, 0}, new int[]{378, 0}, new int[]{369, 0}, new int[]{288, 0},
            new int[]{334, 0}
    ));

    public void openMobdropShop(final Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§7» §8Monsterjägerin Amanda", "§7Wähle eines der aufgelisteten Items aus, welches du kaufen möchtest.");
        this.mobdropShop.forEach(id -> {
            this.shopAPI.getCurrentPrice(id, 1, price -> {
                form.addButton(new ElementButton(Item.get(id[0], id[1]).getName() + "\n§5§l1x §r§f$" + Economy.getAPI().getMoneyFormat().format(price), new ElementButtonImageData("url", "http://45.138.50.23:3000/img/shopitems/" + id[0] + "-" + id[1] + ".png")), e -> {
                    this.openItemShop(e, id);
                });
            });
        });
        form.build().send(player);
    }

    private void playSound(final Player player, final Sound sound) {
        final PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.x = new Double(player.getLocation().getX()).intValue();
        packet.y = (new Double(player.getLocation().getY())).intValue();
        packet.z = (new Double(player.getLocation().getZ())).intValue();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        player.dataPacket(packet);
    }

}
