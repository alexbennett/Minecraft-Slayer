/*
 * Copyright (c) 2013 Alex Bennett
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.alexben.Slayer.Handlers;

import net.alexben.Slayer.Utilities.SConfigUtil;
import net.alexben.Slayer.Utilities.SUtil;
import org.bukkit.Bukkit;

public class SScheduler
{
    @SuppressWarnings("deprecation")
    public static void startThreads()
    {
        // Define variables
        int saveFrequency = SConfigUtil.getSettingInt("save_freq") * 20;

        // Save data
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(SUtil.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                SFlatFile.save();
            }
        }, saveFrequency, saveFrequency);
    }

    public static void stopThreads()
    {
        Bukkit.getServer().getScheduler().cancelTasks(SUtil.getInstance());
    }
}
