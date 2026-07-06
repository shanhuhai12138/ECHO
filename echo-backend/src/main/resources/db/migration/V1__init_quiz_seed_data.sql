-- ============================================
-- ECHO 初始题库种子数据
-- 分类：大五人格简版（10题）
-- 设计依据：心理学 OCEAN 大五人格理论
-- 题目设计原则：行为场景法（通过具体情境推断特质）
-- ============================================

-- 1. 插入分类
INSERT INTO quiz_categories (id, name, description, sort_order, active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '大五人格简版',
    '基于心理学成熟的大五人格理论，用精简行为场景题目刻画你的性格轮廓',
    1,
    true,
    NOW(),
    NOW()
);

-- 获取分类 ID（用于后续题目关联）
WITH cat AS (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1)
SELECT id FROM cat;

-- 2. 插入题目和选项
-- 注意：下面用 placeholder 的 category_id，实际插入时需要替换为上一步查到的 UUID
-- 为方便测试，这里直接用变量方式

-- 题目1：开放性 - 社交场景
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '周末空闲时，你更倾向于？', 'single', 1, true, NOW());

-- 题目1 的选项
INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '周末空闲时%' LIMIT 1),
    '去一家从没去过的小众展览或书店逛逛',
    '{"dimensionKey":"openness","dimensionLabel":"开放性","score":85,"summary":"对新事物充满好奇"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '周末空闲时%' LIMIT 1),
    '在家陪家人或和朋友聚餐聊天',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":60,"summary":"偏好熟悉的社交圈"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '周末空闲时%' LIMIT 1),
    '整理房间、做清洁，把生活打理得井井有条',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":75,"summary":"注重秩序和条理"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '周末空闲时%' LIMIT 1),
    '补觉休息，恢复一周的精力',
    '{"dimensionKey":"energy_level","dimensionLabel":"精力水平","score":40,"summary":"需要充足的恢复时间"}', 4);

-- 题目2：尽责性 - 工作计划
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '面对一项截止日期很近的任务，你会？', 'single', 2, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '面对一项截止%' LIMIT 1),
    '立刻制定计划，分步骤完成',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":90,"summary":"高度自律，执行力强"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '面对一项截止%' LIMIT 1),
    '先做最紧急的部分，剩下的最后赶',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":55,"summary":"灵活应对压力"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '面对一项截止%' LIMIT 1),
    '有点焦虑，但还是会尽力完成',
    '{"dimensionKey":"neuroticism","dimensionLabel":"情绪敏感性","score":60,"summary":"对deadline敏感，有压力反应"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '面对一项截止%' LIMIT 1),
    '拖到最后时刻才开始做',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":20,"summary":"拖延倾向，但可能创造力高"}', 4);

-- 题目3：外向性 - 社交场合
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '参加一个陌生人很多的聚会，你会？', 'single', 3, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '参加一个陌生%' LIMIT 1),
    '主动介绍自己，很快就能融入人群',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":90,"summary":"社交自信，享受与人互动"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '参加一个陌生%' LIMIT 1),
    '先观察一会儿，找到感兴趣的人再聊',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":55,"summary":"选择性社交，有分寸感"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '参加一个陌生%' LIMIT 1),
    '找个角落待着，等熟人来了再说话',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":25,"summary":"偏好深度连接而非广度社交"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '参加一个陌生%' LIMIT 1),
    '找借口早点离开',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":10,"summary":"社交能量有限，需要独处恢复"}', 4);

-- 题目4：宜人性 - 冲突场景
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '和同事意见不合发生争执，你会？', 'single', 4, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '和同事意见不%' LIMIT 1),
    '据理力争，用数据和事实说服对方',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":30,"summary":"坚持己见，重视理性而非和谐"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '和同事意见不%' LIMIT 1),
    '先听对方说完，再表达自己的观点',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":70,"summary":"善于倾听，兼顾双方立场"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '和同事意见不%' LIMIT 1),
    '适当退让，避免关系破裂',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":90,"summary":"重视和谐，愿意妥协"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '和同事意见不%' LIMIT 1),
    '当场表达不满，但事后会主动和解',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":55,"summary":"直率但有修复关系的能力"}', 4);

-- 题目5：情绪稳定性 - 压力场景
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '遇到突发挫折（比如项目被否），你的第一反应是？', 'single', 5, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '遇到突发挫折%' LIMIT 1),
    '有点沮丧，但很快会重新规划',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":75,"summary":"有韧性，能较快恢复"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '遇到突发挫折%' LIMIT 1),
    '很生气，觉得不公平',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":30,"summary":"情绪反应强烈，需要疏导"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '遇到突发挫折%' LIMIT 1),
    '无所谓，反正还有很多机会',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":85,"summary":"心态豁达，不易受挫"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '遇到突发挫折%' LIMIT 1),
    '陷入自我怀疑，担心自己做不好',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":15,"summary":"容易内耗，需要肯定和鼓励"}', 4);

-- 题目6：开放性 - 学习场景
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '学习新技能时，你更喜欢？', 'single', 6, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '学习新技能时%' LIMIT 1),
    '广泛涉猎，了解各种可能性和前沿趋势',
    '{"dimensionKey":"openness","dimensionLabel":"开放性","score":90,"summary":"好奇心驱动，喜欢探索未知"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '学习新技能时%' LIMIT 1),
    '系统学习，按教材和课程一步步来',
    '{"dimensionKey":"openness","dimensionLabel":"开放性","score":40,"summary":"偏好结构化学习路径"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '学习新技能时%' LIMIT 1),
    '边做边学，遇到问题再查资料',
    '{"dimensionKey":"openness","dimensionLabel":"开放性","score":70,"summary":"实践导向的学习者"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '学习新技能时%' LIMIT 1),
    '先想清楚为什么要学，再决定是否投入时间',
    '{"dimensionKey":"openness","dimensionLabel":"开放性","score":60,"summary":"目标驱动，重视学习的意义"}', 4);

-- 题目7：宜人性 - 助人场景
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '朋友向你倾诉烦恼，你会？', 'single', 7, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '朋友向你倾诉%' LIMIT 1),
    '认真倾听，给予情感支持和安慰',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":90,"summary":"共情能力强，善于提供情感支持"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '朋友向你倾诉%' LIMIT 1),
    '帮忙分析问题，给出解决方案',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":65,"summary":"理性助人，注重问题解决"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '朋友向你倾诉%' LIMIT 1),
    '分享自己类似的经历，让对方感到不孤单',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":75,"summary":"善于共情，通过共鸣建立连接"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '朋友向你倾诉%' LIMIT 1),
    '觉得对方应该自己想办法，我不会多管闲事',
    '{"dimensionKey":"agreeableness","dimensionLabel":"宜人性","score":20,"summary":"边界感强，但不愿过度介入"}', 4);

-- 题目8：尽责性 - 生活习惯
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '你的日常生活习惯更接近哪种？', 'single', 8, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '你的日常生活%' LIMIT 1),
    '作息规律，每天按计划行事',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":90,"summary":"高度自律，生活有序"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '你的日常生活%' LIMIT 1),
    '大致有规律，但经常临时改变计划',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":50,"summary":"灵活性强，不拘泥于计划"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '你的日常生活%' LIMIT 1),
    '想到什么做什么，随性自由',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":25,"summary":"随性而为，创造力可能较高"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '你的日常生活%' LIMIT 1),
    '想规律但总是做不到，经常熬夜',
    '{"dimensionKey":"conscientiousness","dimensionLabel":"尽责性","score":40,"summary":"有意愿但执行力有待提升"}', 4);

-- 题目9：外向性 - 表达方式
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '在团队讨论中，你通常是？', 'single', 9, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '在团队讨论中%' LIMIT 1),
    '主动发言，积极表达观点',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":85,"summary":"乐于表达，善于推动讨论"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '在团队讨论中%' LIMIT 1),
    '有人问到时才回答，但回答很有深度',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":45,"summary":"选择性表达，注重质量而非数量"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '在团队讨论中%' LIMIT 1),
    '主要倾听，偶尔补充',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":25,"summary":"善于倾听，内敛型参与者"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '在团队讨论中%' LIMIT 1),
    '会后私下和负责人沟通想法',
    '{"dimensionKey":"extraversion","dimensionLabel":"外向性","score":35,"summary":"偏好一对一深度交流"}', 4);

-- 题目10：情绪稳定性 - 人际评价
INSERT INTO quiz_questions (id, category_id, question_text, question_type, sort_order, active, created_at)
VALUES (gen_random_uuid(), (SELECT id FROM quiz_categories WHERE name = '大五人格简版' LIMIT 1),
    '别人批评你的工作时，你会？', 'single', 10, true, NOW());

INSERT INTO quiz_options (id, question_id, text, score_mapping, sort_order)
VALUES
(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '别人批评你的%' LIMIT 1),
    '虚心接受，反思改进',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":85,"summary":"心态开放，能从批评中成长"}', 1),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '别人批评你的%' LIMIT 1),
    '心里不舒服，但会找机会证明自己',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":50,"summary":"有自尊但也能转化动力"}', 2),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '别人批评你的%' LIMIT 1),
    '觉得对方不了解全貌，想解释清楚',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":40,"summary":"需要被理解，有防御倾向"}', 3),

(gen_random_uuid(), (SELECT id FROM quiz_questions WHERE question_text LIKE '别人批评你的%' LIMIT 1),
    '很受伤，长时间在意别人的看法',
    '{"dimensionKey":"emotional_stability","dimensionLabel":"情绪稳定性","score":15,"summary":"敏感型人格，需要更多肯定"}', 4);