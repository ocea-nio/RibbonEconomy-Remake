package io.github.ilongake.ribboneconomy.core;

import java.util.UUID;

public class TransferService {

    private final DataManager dataManager;

    public TransferService(
            DataManager dataManager
    ) {

        this.dataManager =
                dataManager;
    }

    /**
     * プレイヤー間で送金する
     *
     * @return 送金成功ならtrue
     */
    public boolean transfer(
            UUID sender,
            UUID receiver,
            double amount
    ) {

        // 自分自身への送金は禁止
        if (sender.equals(receiver)) {

            return false;
        }

        // 0以下は禁止
        if (amount <= 0) {

            return false;
        }

        // 送金元からお金を引き出す
        boolean success =
                dataManager.withdraw(
                        sender,
                        amount
                );

        // 残高不足などで失敗
        if (!success) {

            return false;
        }

        // 送金先にお金を追加
        dataManager.addBalance(
                receiver,
                amount
        );

        // データを保存
        dataManager.savePlayer(sender);
        dataManager.savePlayer(receiver);

        return true;
    }
}
