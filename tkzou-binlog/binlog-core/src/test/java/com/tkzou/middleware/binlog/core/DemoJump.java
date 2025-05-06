package com.tkzou.middleware.binlog.core;

/**
 * 给定一个长度为 n 的 0 索引整数数组 nums。初始位置为 nums[0]。
 * <p>
 * 每个元素 nums[i] 表示从索引 i 向前跳转的最大长度。换句话说，如果你在 nums[i] 处，你可以跳转到任意 nums[i + j] 处:
 * <p>
 * 0 <= j <= nums[i]
 * i + j < n
 * 返回到达 nums[n - 1] 的最小跳跃次数。生成的测试用例可以到达 nums[n - 1]。
 * <p>
 * <p>
 * 输入: nums = [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 * 从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 */
public class DemoJump {
    public static void main(String[] args) {
        int[] nums = {2, 3, 1, 1, 4};
        int res = jump(nums);
        System.out.println(res);
    }

    public static int jump(int[] nums) {
        int res = 0;
        int maxPosition = 0;
        int endPosition = 0;
        if (nums == null || nums.length == 0) {
            return 0;
        }
        for (int i = 0; i < nums.length - 1; i++) {
            maxPosition = Math.max(maxPosition, i + nums[i]);
            if (i == endPosition) {
                endPosition = maxPosition;
                res++;
            }
        }
        return res;
    }


}
